package org.remote.desktop.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.model.ELogicalEventType;
import org.remote.desktop.actuate.PointingService;
import org.remote.desktop.actuate.XdoMouseAct;
import org.remote.desktop.model.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.model.EAxisEaser.*;

@Service
@RequiredArgsConstructor
public class FluxUtil {

    private final PointingService pointingService;

    int TRIGGER_EASING_INTERVAL = 141;

    public Flux<RepeatablePolarCoords> repeat(Flux<RepeatablePolarCoords> flux) {
        return repeat(flux, 4);
    }

    public Flux<ButtonActionDef> repeatGE(Flux<ButtonActionDef> flux) {
        return repeat(flux, TRIGGER_EASING_INTERVAL);
    }

    public Flux<ButtonActionDef> repeatGEdge(Flux<ButtonActionDef> flux) {
        AtomicReference<ButtonActionDef> prev = new AtomicReference<>();

        return repeat(flux.mapNotNull(q -> q.getLogicalEventType() == ELogicalEventType.ENGAGE ?
                prev.get() : prev.getAndSet(q)

        ), TRIGGER_EASING_INTERVAL);
    }

    public <T extends Repeatable> Flux<T> repeat(Flux<T> flux, int interval) {
        return flux
                .switchMap(p -> {
                    long period = p instanceof ButtonActionDef e && interval == TRIGGER_EASING_INTERVAL ?
                            (interval - e.getPosition() * 12L) : interval;

                    return !p.isRepeatable() ? Flux.just(p) : // pass (0,0) once, then complete
                            Flux.interval(Duration.ofMillis(period))
                                    .map(_ -> p);
                });
    }

    public Flux<RepeatablePolarCoords> temperedAngularScrolling(Flux<RepeatablePolarCoords> flux) {
        return repeat(adaptForScroll(flux), 21);
    }

    public Flux<RepeatablePolarCoords> adaptForScroll(Flux<RepeatablePolarCoords> flux) {
        return flux.map(adjustRadiusForScroll);
    }

    @Getter(lazy = true)
    private final Map<EAxisEvent, Consumer<RepeatablePolarCoords>> axisEventConsumerMap = Map.of(
                    EAxisEvent.MOUSE, pointingService::moveMouse,
                    EAxisEvent.SCROLL, pointingService::scrollWithStick,
                    EAxisEvent.VOL, e -> System.out.println("lowering volume"),
                    EAxisEvent.NOOP, e -> {}
            );

    public final Map<ETriggerEvent, Consumer<ButtonActionDef>> triggerEventConsumerMap = Map.of(
            ETriggerEvent.VOLUME_DOWN, q -> System.out.println("lowering volume"),
            ETriggerEvent.VOLUME_UP, q -> System.out.println("increasing volume")
    );

    public final Map<EAxisEaser, Function<Flux<RepeatablePolarCoords>, Flux<RepeatablePolarCoords>>> easerMap =
            Map.of(
                    CONTINUOUS, this::repeat,
                    NONE, Function.identity()
            );

    public Map<EAxisEaser, Function<Flux<ButtonActionDef>, Flux<ButtonActionDef>>> GEeaserMap = Map.of(
            CONTINUOUS, this::repeatGE,
            EDGE_STEPPER, this::repeatGEdge,
            NONE, Function.identity()
    );

    Function<RepeatablePolarCoords, RepeatablePolarCoords> adjustRadiusForScroll = polar -> {
        double originalRadius = polar.getRadius();
        double theta = polar.getTheta();

        // Normalize theta to [0, 2π)
        double normalizedTheta = ((theta % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);

        // Calculate scroll speed factor: max at θ = π/2 (up) and θ = 3π/2 (down), min at θ = 0 or π
        double scrollFactor = Math.abs(Math.sin(normalizedTheta));

        // Scale the original radius by the scroll factor
        double newRadius = originalRadius * scrollFactor;

        // Return new PolarCoords with adjusted radius
        return new RepeatablePolarCoords(newRadius, theta, polar.getDevice());
    };


}
