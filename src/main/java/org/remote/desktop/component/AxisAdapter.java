package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.atmosphere.interceptor.InvokationOrder;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.vto.SceneVto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AxisAdapter {

    private final SceneDao sceneDao;
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
        updateAxisConsumers("Base");

        sceneStateRepository.registerSceneObserver(this::updateAxisConsumers);
    }

    void updateAxisConsumers(String windowName) {
        Consumers consumer = cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(windowName, Consumers.class);

        if (Objects.isNull(consumer)) {
            leftStickConsumer = findCoordsConsumer(windowName, SceneVto::getLeftAxisEvent);
            rightStickConsumer = findCoordsConsumer(windowName, SceneVto::getRightAxisEvent);

            cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).put(windowName, new Consumers(rightStickConsumer, leftStickConsumer));
        } else {
            leftStickConsumer = consumer.leftStickConsumer;
            rightStickConsumer = consumer.rightStickConsumer;
        }
    }

    Consumer<PolarCoords> findCoordsConsumer(String windowName, Function<SceneVto, EAxisEvent> getter) {
        return Optional.of(windowName)
                .map(sceneDao::getSceneForWindowNameOrBase)
                .map(axisEventCurrentOrInherited(getter))
                .map(axisEventMap::get)
                .orElse(q -> {});
    }

    Function<SceneVto, EAxisEvent> axisEventCurrentOrInherited(Function<SceneVto, EAxisEvent> getter) {
        return q -> getter.apply(q) == EAxisEvent.INHERITED ?
                getAxisEventOrNoop(q.getInherits(), getter) : getAxisEventOrNoop(q, getter);
    }

    EAxisEvent getAxisEventOrNoop(SceneVto current, Function<SceneVto, EAxisEvent> getter) {
        return Optional.ofNullable(current).map(getter).orElse(EAxisEvent.NOOP);
    }

    public void getRightStickConsumer(PolarCoords pos) {
        rightStickConsumer.accept(pos);
    }

    public void getLeftStickConsumer(PolarCoords pos) {
        leftStickConsumer.accept(pos);
    }

    record Consumers(Consumer<PolarCoords> rightStickConsumer, Consumer<PolarCoords> leftStickConsumer) {}
}
