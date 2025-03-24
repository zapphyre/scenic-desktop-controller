package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.model.PolarCoords;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AxisAdapter {

    private final SceneDao sceneDao;
    private final SettingsDao settingsDao;
    private final SceneStateRepository sceneStateRepository;
    private final CacheManager cacheManager;

    private Consumer<PolarCoords> leftStickConsumer = MouseCtrl::moveMouse;
    private Consumer<PolarCoords> rightStickConsumer = MouseCtrl::scroll;

    private final Map<EAxisEvent, Consumer<PolarCoords>> axisEventMap = Map.of(
            EAxisEvent.MOUSE, MouseCtrl::moveMouse,
            EAxisEvent.SCROLL, MouseCtrl::scroll,
            EAxisEvent.VOL, e -> {},
            EAxisEvent.NOOP, e -> {}
    );

    @PostConstruct
    void init() {
        updateAxisConsumers(settingsDao.getSettings().getBaseSceneName());

        sceneStateRepository.registerRecognizedSceneObserver(this::updateAxisConsumers);
    }

    void updateAxisConsumers(String windowName) {
        Consumers consumers = cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(windowName, Consumers.class);

        if (Objects.isNull(consumers)) {
            EAxisEvent leftAxisEvent = sceneDao.getSceneForWindowNameOrBase(windowName).getLeftAxisEvent();
            EAxisEvent rightAxisEvent = sceneDao.getSceneForWindowNameOrBase(windowName).getRightAxisEvent();

            SceneDto base = sceneDao.getScene(settingsDao.getSettings().getBaseSceneName());

            leftAxisEvent = leftAxisEvent == EAxisEvent.DEFAULT ? base.getLeftAxisEvent() : leftAxisEvent;
            rightAxisEvent = rightAxisEvent == EAxisEvent.DEFAULT ? base.getRightAxisEvent() : rightAxisEvent;

            leftStickConsumer = axisEventMap.get(leftAxisEvent);
            rightStickConsumer = axisEventMap.get(rightAxisEvent);

            cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME)
                    .put(windowName, new Consumers(rightStickConsumer, leftStickConsumer));
        } else {
            leftStickConsumer = consumers.leftStickConsumer;
            rightStickConsumer = consumers.rightStickConsumer;
        }
    }

    public void getRightStickConsumer(PolarCoords pos) {
        rightStickConsumer.accept(pos);
    }

    public void getLeftStickConsumer(PolarCoords pos) {
        leftStickConsumer.accept(pos);
    }

    record Consumers(Consumer<PolarCoords> rightStickConsumer, Consumer<PolarCoords> leftStickConsumer) {}
}
