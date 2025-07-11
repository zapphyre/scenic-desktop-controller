package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.asmus.model.PolarCoords;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@UtilityClass
public class FluxUtil {

    public Flux<PolarCoords> repeat(Flux<PolarCoords> flux) {
        return repeat(flux, PolarCoords::isZero, 4);
    }

    public <T> Flux<T> repeat(Flux<T> flux, Predicate<T> stopWhen, int interval) {
        return flux.switchMap(p -> stopWhen.test(p) ? Flux.just(p) : // pass (0,0) once, then complete
                Flux.interval(Duration.ofMillis(interval))
                        .map(_ -> p));
    }

    public Flux<PolarCoords> temperedAngularScrolling(Flux<PolarCoords> flux) {
        return repeat(adaptForScroll(flux), PolarCoords::isZero, 21);
    }

    public Flux<PolarCoords> adaptForScroll(Flux<PolarCoords> flux) {
        return flux.map(adjustRadiusForScroll);
    }

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
    public static <T> Consumer<T> pipe(Consumer<T> ...consumer) {
        return q -> Arrays.stream(consumer).forEach(p -> p.accept(q));
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
