package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.entity.GesturePath;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.mapper.PolarCoordsMapper;
import org.remote.desktop.model.SourceEvent;
import org.remote.desktop.model.dto.*;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class StickGestureProcessor {

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
                .map(triggerActionMatcher.appEventMapper(buttonAdapter))
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
                                .knownValues(p.getPaths().stream().map(GesturePath::getPath).toList())
                                .key(q.getButtonEvent())
                                .build()).stream()
                )
                .toList();
    }
}
