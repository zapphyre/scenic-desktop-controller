package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.mapper.PolarCoordsMapper;
import org.remote.desktop.model.AppEventMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.SourceEvent;
import org.remote.desktop.model.dto.*;
import org.remote.desktop.model.event.NoopCommandEvent;
import org.remote.desktop.model.event.WinderCommandEvent;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.winder.common.model.EWinderOp;
import org.zapphyre.fizzy.Gesturizer;
import org.zapphyre.fizzy.matcher.Matcher;
import org.zapphyre.fizzy.matcher.build.GestureSupplier;
import org.zapphyre.fizzy.matcher.build.ToleranceConfigurer;
import org.zapphyre.fizzy.model.MatchDef;
import org.zapphyre.fizzy.model.MatchResult;
import org.zapphyre.fizzy.model.ToleranceConfig;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class StickGestureProcessor implements AppEventMapper {

    private final JoyWorker worker;
    private final XdoSceneService xdoSceneService;
    private final SceneService sceneService;
    private final ButtonAdapter buttonAdapter;
    protected final TriggerActionMatcher triggerActionMatcher;
    protected final ApplicationEventPublisher eventPublisher;
    private final ButtonPressMapper buttonPressMapper;
    private final PolarCoordsMapper polarCoordsMapper;
    private final AxisEventProcessorFactory axisEventProcessorFactory;

    @Getter
    private final Sinks.Many<SourceEvent> axis = Sinks.many().multicast().directBestEffort();

    private final ToleranceConfig toleranceConfig = ToleranceConfig.builder()
            .frequencyTolerancePercent(10.0)
            .orderEditDistanceRatio(0.3)
            .maxConsecutiveDrop(2)
            .build();

    private final Gesturizer motionMapper = Gesturizer.withDefaults();

    private Disposable left;
    private Disposable right;

    @PostConstruct
    void init() {
        xdoSceneService.registerRecognizedSceneObserverChange(sceneName -> {
            Optional.ofNullable(left).ifPresent(Disposable::dispose);
            Optional.ofNullable(right).ifPresent(Disposable::dispose);

            left = hookOnStick(axisEventProcessorFactory.leftPolarFlux(), GestureEventDto::getLeftStickGesture, sceneName);
            right = hookOnStick(axisEventProcessorFactory.rightPolarFlux(), GestureEventDto::getRightStickGesture, sceneName);
        });
    }

    Disposable hookOnStick(Flux<PolarCoords> polarCoords, Function<? super GestureEventDto, GestureDto> stickSpecifier, String sceneName) {
        List<MatchDef<ButtonEventDto>> leftMatchDefs = setupMatcherOn(stickSpecifier, sceneName);

        ToleranceConfigurer<ButtonEventDto> forKnownValuesMatcher = Matcher.create(leftMatchDefs);
        Matcher<ButtonEventDto> stringMatcher = forKnownValuesMatcher.withTolerances(toleranceConfig);

        GestureSupplier gs = motionMapper.pathCompose(polarCoords.map(polarCoordsMapper::map));

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

    List<MatchDef<ButtonEventDto>> setupMatcherOn(Function<? super GestureEventDto, GestureDto> stickSpecifier, String sceneName) {
        return Optional.ofNullable(sceneName)
                .map(sceneService::getSceneForWindowNameOrBase)
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

    @Override
    public Function<XdoActionDto, ApplicationEvent> mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction) {
        return q -> {
            GestureEventDto gestureEvent = q.getEvent().getGestureEvent();

            long cnt = Stream.of(gestureEvent.getRightStickGesture(), gestureEvent.getLeftStickGesture())
                    .filter(Objects::nonNull)
                    .count();

            if (cnt < 2)
                return buttonAdapter.mapEvent(def, sceneXdoAction).apply(q);

            if (buffer.remove(gestureEvent))
                return buttonAdapter.mapEvent(def, sceneXdoAction).apply(q);
            else
                buffer.add(gestureEvent);

            Executors.newSingleThreadScheduledExecutor().schedule(buffer::clear, 960, TimeUnit.MILLISECONDS);

            return new NoopCommandEvent(this);
        };
    }
}
