package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.GamepadEvent;
import org.asmus.model.NamingConstants;
import org.asmus.model.PolarCoords;
import org.remote.desktop.actuate.MouseAct;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.remote.desktop.model.EAxisEaser.CONTINUOUS;
import static org.remote.desktop.model.EAxisEaser.NONE;

@UtilityClass
public class FluxUtil {

    int TRIGGER_EASING_INTERVAL  = 141;

    public Flux<PolarCoords> repeat(Flux<PolarCoords> flux) {
        return repeat(flux, PolarCoords::isZero, 4);
    }

    public Flux<ButtonActionDef> repeatGE(Flux<ButtonActionDef> flux) {
        return repeat(flux.filter(onlySteps), q -> q.getPosition() == NamingConstants.MIN, TRIGGER_EASING_INTERVAL);
    }

    Predicate<ButtonActionDef> onlySteps = q -> q.getLogicalEventType() == ELogicalEventType.STEP_NEGATIVE ||
            q.getLogicalEventType() == ELogicalEventType.STEP_POSITIVE;

    public <T> Flux<T> repeat(Flux<T> flux, Predicate<T> stopWhen, int interval) {
        return flux
                .switchMap(p -> {
                    long period = p instanceof ButtonActionDef e && interval == TRIGGER_EASING_INTERVAL ?
                            (interval - e.getPosition() * 12L) : interval;

                    return stopWhen.test(p) ? Flux.just(p) : // pass (0,0) once, then complete
                            Flux.interval(Duration.ofMillis(period))
                                    .map(_ -> p);
                });
    }

    public Flux<PolarCoords> temperedAngularScrolling(Flux<PolarCoords> flux) {
        return repeat(adaptForScroll(flux), PolarCoords::isZero, 21);
    }

    public Flux<PolarCoords> adaptForScroll(Flux<PolarCoords> flux) {
        return flux.map(adjustRadiusForScroll);
    }

    public static final Map<EAxisEvent, Consumer<PolarCoords>> axisEventConsumerMap = Map.of(
            EAxisEvent.MOUSE, MouseAct::moveMouse,
//            EAxisEvent.SCROLL, MouseAct::scroll,
            EAxisEvent.SCROLL, MouseAct::scrollR,
            EAxisEvent.VOL, e -> {
            },
            EAxisEvent.NOOP, e -> {
            }
    );

    public static final Map<EAxisEaser, Function<Flux<PolarCoords>, Flux<PolarCoords>>> easerMap = Map.of(
            CONTINUOUS, FluxUtil::repeat,
            NONE, Function.identity()
    );

    public static final Map<EAxisEaser, Function<Flux<ButtonActionDef>, Flux<ButtonActionDef>>> GEeaserMap = Map.of(
            CONTINUOUS, FluxUtil::repeatGE,
            NONE, Function.identity()
    );

    Function<PolarCoords, PolarCoords> adjustRadiusForScroll = polar -> {
        double originalRadius = polar.getRadius();
        double theta = polar.getTheta();

        // Normalize theta to [0, 2π)
        double normalizedTheta = ((theta % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);

        // Calculate scroll speed factor: max at θ = π/2 (up) and θ = 3π/2 (down), min at θ = 0 or π
        double scrollFactor = Math.abs(Math.sin(normalizedTheta));

        // Scale the original radius by the scroll factor
        double newRadius = originalRadius * scrollFactor;

        // Return new PolarCoords with adjusted radius
        return new PolarCoords(newRadius, theta);
    };

    public static <T> BinaryOperator<T> laterMerger() {
        return (q, p) -> p;
    }

    public <T, R> R optToNull(T id, Function<T, Optional<R>> function) {
        return Optional.ofNullable(id)
                .flatMap(function)
                .orElse(null);
    }

    public static <T, R> Consumer<T> eat(Function<T, R> mapper) {
        return mapper::apply;
    }

    public static <T, R> Consumer<T> chew(Function<T, R> mapper, Consumer<R> sink) {
        return t -> sink.accept(mapper.apply(t));
    }

    public static <T> Function<T, T> funky(Consumer<T> consumer) {
        return q -> Stream.of(q)
                .peek(consumer)
                .findAny()
                .orElse(q);
    }

    @SafeVarargs
    public static <T> Consumer<T> pipe(Consumer<T>... consumer) {
        return q -> Arrays.stream(consumer).forEach(p -> p.accept(q));
    }

    public static <T, R> Function<T, Consumer<Consumer<R>>> spit(Function<T, R> mapper) {
        return q -> p -> p.accept(mapper.apply(q));
    }

    @SafeVarargs
    public static Handover<String> glob(Consumer<Consumer<String>>... delegates) {
        return consumer -> Arrays.stream(delegates).forEach(delegate -> delegate.accept(consumer));
    }

    public <T> Function<Optional<T>, T> orNull() {
        return opt -> opt.orElse(null);
    }

    public interface Handover<T> {
        void to(Consumer<T> consumer);
    }
}
