package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import org.asmus.builder.AxisEventProcessorFactory;
import org.remote.desktop.component.InlineEasingFluxDecorator;
import org.remote.desktop.component.RepeatableDecorator;
import org.remote.desktop.mapper.PolarCoordsMapper;
import org.remote.desktop.model.RepeatablePolarCoords;
import org.remote.desktop.model.dto.SceneDto;
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

    private final SceneService sceneService;
    private final XdoSceneService xdoSceneService;
    private final AxisEventProcessorFactory axisEventProcessorFactory;

    private final InlineEasingFluxDecorator<RepeatablePolarCoords> leftRepeater;
    private final InlineEasingFluxDecorator<RepeatablePolarCoords> rightRepeater;

    RepeatableDecorator<RepeatablePolarCoords> decorator;

    public RepeatingAxisAdapter(SceneService sceneService, XdoSceneService xdoSceneService,
                                AxisEventProcessorFactory axisEventProcessorFactory, ScheduledExecutorService executorService,
                                CacheManager cacheManager, PolarCoordsMapper polarCoordsMapper) {
        this.sceneService = sceneService;
        this.xdoSceneService = xdoSceneService;
        this.axisEventProcessorFactory = axisEventProcessorFactory;

        decorator = new RepeatableDecorator<>(
                cacheManager,
                axisEventProcessorFactory.leftPolarFlux().map(polarCoordsMapper::mapRep),
                easerMap,
                SceneDto::getLeftAxisEaser,
                axisEventConsumerMap,
                SceneDto::getLeftAxisEvent
        );

        RepeatableDecorator<RepeatablePolarCoords> repeated = new RepeatableDecorator<>(
                cacheManager,
                decorator.getRepeatingStream(),
                easerMap,
                SceneDto::getLeftAxisEaser,
                axisEventConsumerMap,
                SceneDto::getLeftAxisEvent
        );

        this.rightRepeater = new InlineEasingFluxDecorator<>(
                cacheManager,
                axisEventProcessorFactory.rightPolarFlux().map(polarCoordsMapper::mapRep),
                easerMap,
                SceneDto::getRightAxisEaser,
                axisEventConsumerMap,
                SceneDto::getRightAxisEvent
        );

        this.leftRepeater = new InlineEasingFluxDecorator<>(
                cacheManager,
                axisEventProcessorFactory.leftPolarFlux().map(polarCoordsMapper::mapRep),
                easerMap,
                SceneDto::getLeftAxisEaser,
                axisEventConsumerMap,
                SceneDto::getLeftAxisEvent
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
//        glob(xdoSceneService::registerRecognizedSceneObserverChange, xdoSceneService::registerForcedSceneObserver)
//                .to(chew(sceneService::getSceneForWindowNameOrBase, pipe(q -> {
//                        Optional.ofNullable(left).ifPresent(Disposable::dispose);
//                    left = decorator.getRepeatingStream()
//                            .subscribe(axisEventConsumerMap.get(q.getLeftAxisEvent()));

//                    Flux.just(q)
//                            .switchMap(scene -> decorator.getRepeatingStream()
//                                    .doOnNext(axisEventConsumerMap.get(q.getLeftAxisEvent()))
//                            )
//                            .subscribe(p -> {
//                                System.out.println(p);
//                                axisEventConsumerMap.get(q.getLeftAxisEvent()).accept(p);
//                            });
//                            .subscribe();
//                })));

        glob(xdoSceneService::registerRecognizedSceneObserverChange, xdoSceneService::registerForcedSceneObserver)
                .to(chew(sceneService::getSceneForWindowNameOrBase, pipe(leftRepeater::setScene, rightRepeater::setScene)));
    }
}