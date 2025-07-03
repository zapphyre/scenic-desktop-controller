package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.asmus.model.PolarCoords;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@UtilityClass
public class FluxUtil {

    public <T> Flux<T> repeat(Flux<T> flux, Predicate<T> stopWhen, int interval) {
        return flux.switchMap(p -> stopWhen.test(p) ? Flux.just(p) : // pass (0,0) once, then complete
                Flux.interval(Duration.ofMillis(interval))
                        .map(i -> p));
    }

    public Function<PolarCoords, Flux<PolarCoords>> repeat(Predicate<PolarCoords> stopWhen) {
        AtomicReference<Double> prev = new AtomicReference<>(0D);

        return q -> {
            double delta = Math.abs(q.getRadius() - prev.get());
            Double pre = prev.getAndSet(delta);
            //                stopWhen.test(q) ? Flux.just(q) : // pass (0,0) once, then complete
//            return Flux.interval(Duration.ofMillis((long) mapClamped(delta, 0, pre + delta, 15, 3)))
               return Flux.interval(Duration.ofMillis(3))
                    .map(i -> q.withRadius(delta))
                    .takeUntil(stopWhen);
        };
    }

    public static <T> BinaryOperator<T> laterMerger() {
        return (q, p) -> p;
    }

    public <T, R> R optToNull(T id, Function<T, Optional<R>> function) {
        return Optional.ofNullable(id)
                .flatMap(function)
                .orElse(null);
    }
    public static <T, R> Consumer<T> asConsumer(Function<T, R> mapper) {
        return mapper::apply;
    }

    public static <T> Function<T, T> asFun(Consumer<T> consumer) {
        return q -> Stream.of(q)
                .peek(consumer)
                .findFirst()
                .orElse(q);
    }

    public <T> Function<Optional<T>, T> orNull() {
        return opt -> opt.orElse(null);
    }
}
