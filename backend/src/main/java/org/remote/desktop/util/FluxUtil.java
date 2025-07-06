package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.asmus.model.PolarCoords;
import reactor.core.publisher.Flux;

import java.time.Duration;
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
                        .map(i -> p));
    }

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

    public <T> Function<Optional<T>, T> orNull() {
        return opt -> opt.orElse(null);
    }
}
