package org.remote.desktop.processor;

import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.AxisReading;
import org.remote.desktop.component.InlineEasingFluxDecorator;
import org.remote.desktop.mapper.PolarCoordsMapper;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.ModeService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.util.FluxUtil;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static org.zapphyre.function.FunHelper.*;

@Component
public class RepeatingAxisAdapter {

    private final AxisEventProcessorFactory axisEventProcessorFactory;
    private final FluxUtil fluxUtil;

    public RepeatingAxisAdapter(SceneService sceneService, XdoSceneService xdoSceneService,
                                AxisEventProcessorFactory axisEventProcessorFactory, CacheManager cacheManager,
                                PolarCoordsMapper polarCoordsMapper, ModeService modeService, FluxUtil fluxUtil) {
        this.axisEventProcessorFactory = axisEventProcessorFactory;
        this.fluxUtil = fluxUtil;

        for (GamepadDto g : modeService.getAllGamepads()) {
            var right = new InlineEasingFluxDecorator<>(
                    cacheManager,
                    axisEventProcessorFactory.rightPolarFlux()
                            .filter(q -> q.getDevice().name().equals(g.getName()))
                            .map(polarCoordsMapper::mapRep),
                    fluxUtil.easerMap,
                    SceneDto::getRightAxisEaser,
                    fluxUtil.getAxisEventConsumerMap(),
                    SceneDto::getRightAxisEvent
            );

            var left = new InlineEasingFluxDecorator<>(
                    cacheManager,
                    axisEventProcessorFactory.leftPolarFlux()
                            .filter(q -> q.getDevice().name().equals(g.getName()))
                            .map(polarCoordsMapper::mapRep),
                    fluxUtil.easerMap,
                    SceneDto::getLeftAxisEaser,
                    fluxUtil.getAxisEventConsumerMap(),
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