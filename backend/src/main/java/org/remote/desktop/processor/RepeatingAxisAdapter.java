package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.PolarCoords;
import org.remote.desktop.component.GrokFluxRepeater;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static org.remote.desktop.util.FluxUtil.*;

@Component
public class RepeatingAxisAdapter {

    private final SceneService sceneService;
    private final XdoSceneService xdoSceneService;
    private final AxisEventProcessorFactory axisEventProcessorFactory;

    private final GrokFluxRepeater<PolarCoords> leftRepeater;
    private final GrokFluxRepeater<PolarCoords> rightRepeater;

    public RepeatingAxisAdapter(SceneService sceneService, XdoSceneService xdoSceneService,
                                AxisEventProcessorFactory axisEventProcessorFactory, ScheduledExecutorService executorService,
                                CacheManager cacheManager) {
        this.sceneService = sceneService;
        this.xdoSceneService = xdoSceneService;
        this.axisEventProcessorFactory = axisEventProcessorFactory;

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