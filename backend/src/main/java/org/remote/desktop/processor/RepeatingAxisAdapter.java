package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.AxisReading;
import org.remote.desktop.component.InlineEasingFluxDecorator;
import org.remote.desktop.mapper.PolarCoordsMapper;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.RepeatablePolarCoords;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.ModeService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static org.remote.desktop.util.FluxUtil.axisEventConsumerMap;
import static org.remote.desktop.util.FluxUtil.easerMap;
import static org.zapphyre.function.FunHelper.*;

@Component
public class RepeatingAxisAdapter {

    private final AxisEventProcessorFactory axisEventProcessorFactory;

    public RepeatingAxisAdapter(SceneService sceneService, XdoSceneService xdoSceneService,
                                AxisEventProcessorFactory axisEventProcessorFactory, ScheduledExecutorService executorService,
                                CacheManager cacheManager, PolarCoordsMapper polarCoordsMapper, ModeService modeService) {
        this.axisEventProcessorFactory = axisEventProcessorFactory;

        for (GamepadDto g : modeService.getAllGamepads()) {
            InlineEasingFluxDecorator<EAxisEvent, RepeatablePolarCoords> right = new InlineEasingFluxDecorator<>(
                    cacheManager,
                    axisEventProcessorFactory.rightPolarFlux()
                            .filter(q -> q.getDevice().name().equals(g.getName()))
                            .map(polarCoordsMapper::mapRep),
                    easerMap,
                    SceneDto::getRightAxisEaser,
                    axisEventConsumerMap,
                    SceneDto::getRightAxisEvent
            );

            InlineEasingFluxDecorator<EAxisEvent, RepeatablePolarCoords> left = new InlineEasingFluxDecorator<>(
                    cacheManager,
                    axisEventProcessorFactory.leftPolarFlux()
                            .filter(q -> q.getDevice().name().equals(g.getName()))
                            .map(polarCoordsMapper::mapRep),
                    easerMap,
                    SceneDto::getLeftAxisEaser,
                    axisEventConsumerMap,
                    SceneDto::getLeftAxisEvent
            );

            glob(xdoSceneService::registerRecognizedSceneObserverChange, xdoSceneService::registerForcedSceneObserver)
                    .to(chew(sceneService.getSceneForModeAndWindowNameOrBase(modeService.getAllGamepads().getFirst()), pipe(left::setScene, right::setScene)));

        }
    }

    public Consumer<AxisReading> leftAxis() {
        return axisEventProcessorFactory.leftStickStream()::processArrowEvents;
    }

    public Consumer<AxisReading> rightAxis() {
        return axisEventProcessorFactory.reightStickStream()::processArrowEvents;
    }
}