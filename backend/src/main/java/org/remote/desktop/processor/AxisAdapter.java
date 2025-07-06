package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.PolarCoords;
import org.remote.desktop.actuate.MouseAct;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.util.FluxUtil;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.remote.desktop.model.EAxisEaser.CONTINUOUS;
import static org.remote.desktop.util.FluxUtil.chew;

@Component
@RequiredArgsConstructor
public class AxisAdapter {

    private final SceneService sceneService;
    private final SettingsDao settingsDao;
    private final XdoSceneService xdoSceneService;
    private final AxisEventProcessorFactory axisEventProcessorFactory;
    private final CacheManager cacheManager;

    @Getter
    private Consumer<PolarCoords> leftStickConsumer = MouseAct::moveMouse;
    @Getter
    private Consumer<PolarCoords> rightStickConsumer = MouseAct::scroll;

    private Disposable leftStick;
    private Disposable rightStick;


    private final Map<EAxisEvent, Consumer<PolarCoords>> axisEventConsumerMap = Map.of(
            EAxisEvent.MOUSE, MouseAct::moveMouse,
            EAxisEvent.SCROLL, MouseAct::scroll,
            EAxisEvent.VOL, e -> {
            },
            EAxisEvent.NOOP, e -> {
            }
    );

    private final Map<EAxisEaser, Function<Flux<PolarCoords>, Flux<PolarCoords>>> easerMap = Map.of(
            CONTINUOUS, FluxUtil::repeat
    );

    @PostConstruct
    void init() {
        xdoSceneService.registerRecognizedSceneObserverChange(forKeyOnScene(sceneService::getSceneForWindowNameOrBase));
        xdoSceneService.registerForcedSceneObserver(forKeyOnScene(sceneService::getScene));
    }

    Consumer<String> forKeyOnScene(Function<String, SceneDto> sceneGetter) {
        return chew(sceneGetter.andThen(getCachedOrFreshConsumers()), this::constructEasedPipeline);
    }

    void constructEasedPipeline(Consumers consumers) {
        Stream.of(leftStick, rightStick).filter(Objects::nonNull).forEach(Disposable::dispose);

        leftStick = consumers.leftEasing.apply(axisEventProcessorFactory.leftPolarFlux())
                .subscribe(consumers.leftStickConsumer);

        rightStick = consumers.rightEasing.apply(axisEventProcessorFactory.rightPolarFlux())
                .subscribe(consumers.rightStickConsumer);
    }

    Function<SceneDto, Consumers> getCachedOrFreshConsumers() {
        return q -> getAxisCachedConsumers(q.getName()) instanceof Consumers c ?
                c : getConsumersAndCache(q);
    }

    Consumers getAxisCachedConsumers(String key) {
        return cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(key, Consumers.class);
    }

    Consumers getConsumersAndCache(SceneDto sceneDto) {
        EAxisEvent leftAxisEvent = sceneDto.getLeftAxisEvent();
        EAxisEvent rightAxisEvent = sceneDto.getRightAxisEvent();
        EAxisEaser leftAxisEaser = sceneDto.getLeftAxisEaser();
        EAxisEaser rightAxisEaser = sceneDto.getRightAxisEaser();

        SceneDto base = sceneService.getScene(settingsDao.getSettings().getBaseSceneName());

        leftAxisEvent = leftAxisEvent == EAxisEvent.DEFAULT ? base.getLeftAxisEvent() : leftAxisEvent;
        rightAxisEvent = rightAxisEvent == EAxisEvent.DEFAULT ? base.getRightAxisEvent() : rightAxisEvent;

        Consumers consumers = new Consumers(
                axisEventConsumerMap.get(rightAxisEvent),
                axisEventConsumerMap.get(leftAxisEvent),
                easerMap.getOrDefault(rightAxisEaser, Function.identity()),
                easerMap.get(CONTINUOUS)
        );

        cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME)
                .put(sceneDto.getName(), consumers);

        return consumers;
    }

    record Consumers(Consumer<PolarCoords> rightStickConsumer, Consumer<PolarCoords> leftStickConsumer,
                     Function<Flux<PolarCoords>, Flux<PolarCoords>> rightEasing,
                     Function<Flux<PolarCoords>, Flux<PolarCoords>> leftEasing) {
    }
}
