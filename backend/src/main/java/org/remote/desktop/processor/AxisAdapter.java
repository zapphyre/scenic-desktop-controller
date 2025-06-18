package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.PolarCoords;
import org.remote.desktop.actuate.MouseAct;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AxisAdapter {

    private final SceneService sceneService;
    private final SettingsDao settingsDao;
    private final XdoSceneService xdoSceneService;
    private final IntrospectedEventFactory gamepadObserver;
    private final TriggerActionMatcher triggerActionMatcher;
    private final ButtonPressMapper buttonPressMapper;
    private final CacheManager cacheManager;

    @Getter
    private Consumer<PolarCoords> leftStickConsumer = MouseAct::moveMouse;
    @Getter
    private Consumer<PolarCoords> rightStickConsumer = MouseAct::scroll;


    private final Map<EAxisEvent, Consumer<PolarCoords>> axisEventMap = Map.of(
            EAxisEvent.MOUSE, MouseAct::moveMouse,
            EAxisEvent.SCROLL, MouseAct::scroll,
            EAxisEvent.VOL, e -> {
            },
            EAxisEvent.NOOP, e -> {
            }
    );


    @PostConstruct
    void init() {
        xdoSceneService.registerRecognizedSceneObserverChange(this::updateAxisConsumersByWindowName);
        xdoSceneService.registerForcedSceneObserver(this::updateAxisConsumersBySceneName);
    }

    public Consumer<Map<String, Integer>> getLeftStickProcessor() {
        return gamepadObserver.leftStickStream()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getRightStickProcessor() {
        return gamepadObserver.rightStickStream()::processArrowEvents;
    }

    void setConsumersFor(SceneDto sceneDto, String key) {
        EAxisEvent leftAxisEvent = sceneDto.getLeftAxisEvent();
        EAxisEvent rightAxisEvent = sceneDto.getRightAxisEvent();

        SceneDto base = sceneService.getScene(settingsDao.getSettings().getBaseSceneName());

        leftAxisEvent = leftAxisEvent == EAxisEvent.DEFAULT ? base.getLeftAxisEvent() : leftAxisEvent;
        rightAxisEvent = rightAxisEvent == EAxisEvent.DEFAULT ? base.getRightAxisEvent() : rightAxisEvent;

        leftStickConsumer = axisEventMap.get(leftAxisEvent);
        rightStickConsumer = axisEventMap.get(rightAxisEvent);

        cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME)
                .put(key, new Consumers(rightStickConsumer, leftStickConsumer));
    }

    void updateAxisConsumersBySceneName(String sceneName) {
        Consumers consumers = cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(sceneName, Consumers.class);

        if (Objects.isNull(consumers)) {
            SceneDto sceneByName = sceneService.getScene(sceneName);

            setConsumersFor(sceneByName, sceneName);
        } else
            setConsumers(consumers);

//        Objects.requireNonNull(cacheManager.getCache(SceneDao.SCENE_ACTIONS_CACHE_NAME)).clear();
    }

    void updateAxisConsumersByWindowName(String windowName) {
        Consumers consumers = cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(windowName, Consumers.class);

        if (Objects.isNull(consumers)) {
            SceneDto sceneForWindowNameOrBase = sceneService.getSceneForWindowNameOrBase(windowName);

            setConsumersFor(sceneForWindowNameOrBase, windowName);
        } else
            setConsumers(consumers);
    }

    void setConsumers(Consumers consumers) {
        leftStickConsumer = consumers.leftStickConsumer;
        rightStickConsumer = consumers.rightStickConsumer;
    }

    record Consumers(Consumer<PolarCoords> rightStickConsumer, Consumer<PolarCoords> leftStickConsumer) {
    }
}
