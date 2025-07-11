package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.PolarCoords;
import org.remote.desktop.actuate.MouseAct;
import org.remote.desktop.component.FluxRepeater;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.model.EAxisEaser.CONTINUOUS;
import static org.remote.desktop.model.EAxisEaser.NONE;
import static org.remote.desktop.util.FluxUtil.glob;
import static org.remote.desktop.util.FluxUtil.pipe;

@Component
public class AxisAdapter {

    private final SceneService sceneService;
    private final SettingsDao settingsDao;
    private final XdoSceneService xdoSceneService;
    private final AxisEventProcessorFactory axisEventProcessorFactory;
    protected final ScheduledExecutorService executorService;

    private final FluxRepeater<PolarCoords> leftRepeater;
    private final FluxRepeater<PolarCoords> rightRepeater;
    private final CacheManager cacheManager;

    private Consumer<PolarCoords> leftStickConsumer = MouseAct::moveMouse;
    private Consumer<PolarCoords> rightStickConsumer = MouseAct::scroll;

    private Disposable leftStick;
    private Disposable rightStick;

    public AxisAdapter(SceneService sceneService, SettingsDao settingsDao, XdoSceneService xdoSceneService,
                       AxisEventProcessorFactory axisEventProcessorFactory, ScheduledExecutorService executorService,
                       CacheManager cacheManager) {
        this.sceneService = sceneService;
        this.settingsDao = settingsDao;
        this.xdoSceneService = xdoSceneService;
        this.axisEventProcessorFactory = axisEventProcessorFactory;
        this.executorService = executorService;
        this.cacheManager = cacheManager;

        this.leftRepeater = new FluxRepeater<>(
                cacheManager, axisEventProcessorFactory.leftPolarFlux(), easerMap, SceneDto::getLeftAxisEaser
        );
        this.rightRepeater = new FluxRepeater<>(
                cacheManager, axisEventProcessorFactory.rightPolarFlux(), easerMap, SceneDto::getRightAxisEaser
        );
    }

    public Consumer<Map<String, Integer>> leftAxis() {
        return axisEventProcessorFactory.leftStickStream()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> rightAxis() {
        return axisEventProcessorFactory.reightStickStream()::processArrowEvents;
    }

    private final Map<EAxisEvent, Consumer<PolarCoords>> axisEventConsumerMap = Map.of(
            EAxisEvent.MOUSE, MouseAct::moveMouse,
//            EAxisEvent.SCROLL, MouseAct::scroll,
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


    @PostConstruct
    void init() {
        Consumer<String> left = repeatingStreamConsumer(leftRepeater).apply(SceneDto::getLeftAxisEvent);
        Consumer<String> right = repeatingStreamConsumer(rightRepeater).apply(SceneDto::getRightAxisEvent);

        glob(xdoSceneService::registerRecognizedSceneObserverChange, xdoSceneService::registerForcedSceneObserver)
                .to(pipe(left, right));
    }

    Function<SceneAxisGetter, Consumer<String>> repeatingStreamConsumer(FluxRepeater<PolarCoords> repeater) {
        AxisSpecifier specify = decorateConsumer().source(repeater);
        return p -> repeater.forKeyOnScene(sceneService::getSceneForWindowNameOrBase, specify.axis(p));
    }

    DecoratingConsumer<PolarCoords> decorateConsumer() {
        AtomicReference<Disposable> disposable = new AtomicReference<>();
        return repeater -> axisGetter -> scene -> {
//            if (disposable.get() != null) disposable.get().dispose();

            disposable.set(repeater.getRepeatingStream()
                    .subscribe(axisEventConsumerMap.get(axisGetter.axisGetter(scene))));
        };
    }

    @FunctionalInterface
    interface SceneAxisGetter {
        EAxisEvent axisGetter(SceneDto sceneDto);

    }

    @FunctionalInterface
    interface AxisSpecifier {
        Consumer<SceneDto> axis(SceneAxisGetter getter);
    }

    @FunctionalInterface
    interface DecoratingConsumer<T> {
        AxisSpecifier source(FluxRepeater<T> repeater);
    }

}
