package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.PolarCoords;
import org.remote.desktop.actuate.MouseAct;
import org.remote.desktop.component.GrokFluxRepeater;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.util.FluxUtil;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.model.EAxisEaser.CONTINUOUS;
import static org.remote.desktop.model.EAxisEaser.NONE;
import static org.remote.desktop.util.FluxUtil.*;

@Component
public class RepeatingAxisAdapter {

    private final SceneService sceneService;
    private final SettingsDao settingsDao;
    private final XdoSceneService xdoSceneService;
    private final AxisEventProcessorFactory axisEventProcessorFactory;
    private final ScheduledExecutorService executorService;
    private final CacheManager cacheManager;

    private final GrokFluxRepeater<PolarCoords> leftRepeater;
    private final GrokFluxRepeater<PolarCoords> rightRepeater;

    private final Map<EAxisEvent, Consumer<PolarCoords>> axisEventConsumerMap = Map.of(
            EAxisEvent.MOUSE, MouseAct::moveMouse,
            EAxisEvent.SCROLL, MouseAct::scrollR,
            EAxisEvent.VOL, e -> {
            },
            EAxisEvent.NOOP, e -> {
            }
    );

    private final Map<EAxisEaser, Function<Flux<PolarCoords>, Flux<PolarCoords>>> easerMap = Map.of(
            CONTINUOUS, FluxUtil::repeat,
            NONE, Function.identity()
    );

    public RepeatingAxisAdapter(SceneService sceneService, SettingsDao settingsDao, XdoSceneService xdoSceneService,
                                AxisEventProcessorFactory axisEventProcessorFactory, ScheduledExecutorService executorService,
                                CacheManager cacheManager) {
        this.sceneService = sceneService;
        this.settingsDao = settingsDao;
        this.xdoSceneService = xdoSceneService;
        this.axisEventProcessorFactory = axisEventProcessorFactory;
        this.executorService = executorService;
        this.cacheManager = cacheManager;

        this.leftRepeater = new GrokFluxRepeater<>(
                cacheManager,
                axisEventProcessorFactory.leftPolarFlux(),
                easerMap,
                SceneDto::getLeftAxisEaser,
                axisEventConsumerMap,
                SceneDto::getLeftAxisEvent
        );

        this.rightRepeater = new GrokFluxRepeater<>(
                cacheManager,
                axisEventProcessorFactory.rightPolarFlux(),
                easerMap,
                SceneDto::getRightAxisEaser,
                axisEventConsumerMap,
                SceneDto::getRightAxisEvent
        );
    }

    public Consumer<Map<String, Integer>> leftAxis() {
        return axisEventProcessorFactory.leftStickStream()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> rightAxis() {
        return axisEventProcessorFactory.reightStickStream()::processArrowEvents;
    }

    @PostConstruct
    void init() {
        glob(xdoSceneService::registerRecognizedSceneObserverChange, xdoSceneService::registerForcedSceneObserver)
                .to(chew(sceneService::getSceneForWindowNameOrBase, pipe(leftRepeater::setScene, rightRepeater::setScene)));
    }
}