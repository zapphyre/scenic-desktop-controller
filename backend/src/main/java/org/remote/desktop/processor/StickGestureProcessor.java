package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.PolarCoords;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.mapper.PolarCoordsMapper;
import org.remote.desktop.model.AppEventMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.*;
import org.remote.desktop.model.event.NoopCommandEvent;
import org.remote.desktop.service.impl.ModeService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zapphyre.fizzy.Gesturizer;
import org.zapphyre.fizzy.matcher.Matcher;
import org.zapphyre.fizzy.matcher.build.GestureSupplier;
import org.zapphyre.fizzy.model.MatchDef;
import org.zapphyre.fizzy.model.MatchResult;
import org.zapphyre.fizzy.model.ToleranceConfig;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class StickGestureProcessor implements AppEventMapper {

    private final AxisEventProcessorFactory axisEventProcessorFactory;
    private final TriggerActionMatcher triggerActionMatcher;
    private final XdoSceneService xdoSceneService;
    private final ButtonAdapter buttonAdapter;
    private final SceneService sceneService;
    private final ModeService modeService;
    private final ApplicationEventPublisher eventPublisher;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ButtonPressMapper buttonPressMapper;
    private final PolarCoordsMapper polarCoordsMapper;
    private final Scheduler scheduler;

    private final ToleranceConfig toleranceConfig = ToleranceConfig.builder()
            .frequencyTolerancePercent(10.0)
            .orderEditDistanceRatio(0.3)
            .maxConsecutiveDrop(2)
            .build();

    private final Gesturizer motionMapper = Gesturizer.withDefaults();

    @PostConstruct
    void init() {
        Map<GamepadDto, Disposable> left = new HashMap<>();
        Map<GamepadDto, Disposable> right = new HashMap<>();

        xdoSceneService.registerRecognizedSceneObserverChange(sceneName -> {
            for (GamepadDto g : modeService.getAllGamepads()) {
                Optional.ofNullable(left.get(g)).ifPresent(Disposable::dispose);
                Optional.ofNullable(right.get(g)).ifPresent(Disposable::dispose);

                left.put(g, hookOnStick(axisEventProcessorFactory.leftPolarFlux(), GestureEventDto::getLeftStickGesture, sceneName, g));
                right.put(g, hookOnStick(axisEventProcessorFactory.rightPolarFlux(), GestureEventDto::getRightStickGesture, sceneName, g));
            }
        });
    }

    Disposable hookOnStick(Flux<PolarCoords> polarCoords, Function<? super GestureEventDto, GestureDto> stickSpecifier, String sceneName, GamepadDto g) {
        List<MatchDef<ButtonEventDto>> defs = setupMatcherOn(stickSpecifier, sceneName, g);

        Matcher<ButtonEventDto> stringMatcher = Matcher.create(defs).withTolerances(toleranceConfig);

        Flux<org.zapphyre.model.PolarCoords> coordsFlux = polarCoords
                .filter(q -> q.getDevice().name().equals(g.getName()))
                .map(polarCoordsMapper::map)
                .subscribeOn(scheduler);

        GestureSupplier gs = motionMapper.pathCompose(coordsFlux);

        return gs.gestureCb(o -> stringMatcher.match(o).stream()
                .filter(q -> q.getMatchPercentage() >= 80d)
                .peek(q -> log.info("Match: {}", q))
                .findFirst().stream()
                .map(MatchResult::getKey)
                .map(buttonPressMapper::map)
                .map(triggerActionMatcher.appEventMapper(this))
                .flatMap(Collection::stream)
                .forEach(eventPublisher::publishEvent)
        );
    }

    List<MatchDef<ButtonEventDto>> setupMatcherOn(Function<? super GestureEventDto, GestureDto> stickSpecifier, String sceneName, GamepadDto g) {
        return Optional.ofNullable(sceneName)
                .map(sceneService.getSceneForModeAndWindowNameOrBase(g))
                .map(SceneDto::getEvents)
                .orElseGet(Collections::emptyList).stream()
                .flatMap(q -> Optional.ofNullable(q)
                        .map(EventDto::getGestureEvent)
                        .map(stickSpecifier)
                        .map(p -> MatchDef.<ButtonEventDto>builder()
                                .knownValues(p.getPaths().stream().map(GesturePathDto::getPath).toList())
                                .key(q.getButtonEvent())
                                .build()).stream()
                )
                .toList();
    }

    private final List<GestureEventDto> buffer = new LinkedList<>();

    private ScheduledFuture<?> scheduled;

    @Override
    public Function<XdoActionDto, ApplicationEvent> mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction) {
        return q -> {
            GestureEventDto gestureEvent = q.getEvent().getGestureEvent();
            Optional.ofNullable(scheduled)
                    .ifPresent(s -> s.cancel(true));

            long cnt = Stream.of(gestureEvent.getRightStickGesture(), gestureEvent.getLeftStickGesture())
                    .filter(Objects::nonNull)
                    .count();

            if (cnt < 2)
                return buttonAdapter.mapEvent(def, sceneXdoAction).apply(q);

            if (buffer.remove(gestureEvent))
                return buttonAdapter.mapEvent(def, sceneXdoAction).apply(q);
            else
                buffer.add(gestureEvent);

            scheduled = scheduledExecutorService.schedule(buffer::clear, 960, TimeUnit.MILLISECONDS);

            return new NoopCommandEvent(this);
        };
    }
}
