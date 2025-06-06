package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.GamepadEvent;
import org.asmus.model.PolarCoords;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.ELogicalTrigger;
import org.remote.desktop.model.Node;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.XdoSceneService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.asmus.model.EButtonAxisMapping.LEFT_STICK_X;
import static org.asmus.model.EButtonAxisMapping.TRIGGER_LEFT;
import static org.remote.desktop.util.ETriggerFilter.triggerBetween;
import static org.remote.desktop.util.GestureUtil.buildNodeMap;
import static org.remote.desktop.util.GestureUtil.gestures;

@Component
@RequiredArgsConstructor
public class AxisAdapter {

    private final SceneDao sceneDao;
    private final SettingsDao settingsDao;
    private final XdoSceneService xdoSceneService;
    private final IntrospectedEventFactory gamepadObserver;
    private final TriggerActionMatcher triggerActionMatcher;
    private final ButtonPressMapper buttonPressMapper;
    private final CacheManager cacheManager;

    private Map<ELogicalTrigger, Node> nodeMap, original;

    @Getter
    private Consumer<PolarCoords> leftStickConsumer = MouseCtrl::moveMouse;
    //    private Consumer<PolarCoords> leftStickConsumer = (q) -> {
//    };
    @Getter
    private Consumer<PolarCoords> rightStickConsumer = MouseCtrl::scroll;
//    private Consumer<PolarCoords> rightStickConsumer = (q) -> {
//    };


    private final Map<EAxisEvent, Consumer<PolarCoords>> axisEventMap = Map.of(
            EAxisEvent.MOUSE, MouseCtrl::moveMouse,
            EAxisEvent.SCROLL, MouseCtrl::scroll,
            EAxisEvent.VOL, e -> {
            },
            EAxisEvent.NOOP, e -> {
            }
    );


    @PostConstruct
    void init() {
        xdoSceneService.registerRecognizedSceneObserver(this::updateAxisConsumersByWindowName);
        xdoSceneService.registerForcedSceneObserver(this::updateAxisConsumersBySceneName);

        nodeMap = original = buildNodeMap(gestures);

        Flux<GamepadEvent> digitized = gamepadObserver.getButtonEventStream()
                .filter(triggerBetween(LEFT_STICK_X, TRIGGER_LEFT))
                .distinctUntilChanged();

        digitized
                .window(Duration.ofMillis(210))
                .flatMap(Flux::collectList)
                .filter(List::isEmpty)
                .subscribe(q -> nodeMap = original);

        digitized.map(buttonPressMapper::map)
                .mapNotNull(ButtonActionDef::getLogicalTrigger)
                .mapNotNull(q -> nodeMap.get(q)) // can't use method reference b/c I am re-assigning nodeMap
                .doOnNext(q -> nodeMap = q.getConnections())
                .mapNotNull(Node::getActions)
                .flatMap(Flux::fromIterable)
                .subscribe(q -> System.out.println("apply action: " + q));
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

        SceneDto base = sceneDao.getScene(settingsDao.getSettings().getBaseSceneName());

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
            SceneDto sceneByName = sceneDao.getScene(sceneName);

            setConsumersFor(sceneByName, sceneName);
        } else
            setConsumers(consumers);

        Objects.requireNonNull(cacheManager.getCache(SceneDao.SCENE_ACTIONS_CACHE_NAME)).clear();
    }

    void updateAxisConsumersByWindowName(String windowName) {
        Consumers consumers = cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(windowName, Consumers.class);

        if (Objects.isNull(consumers)) {
            SceneDto sceneForWindowNameOrBase = sceneDao.getSceneForWindowNameOrBase(windowName);

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
