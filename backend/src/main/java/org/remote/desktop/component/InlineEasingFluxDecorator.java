package org.remote.desktop.component;

import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.Repeatable;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.util.TextUtil.extractMethodName;
import static org.zapphyre.function.FunHelper.funky;
import static org.zapphyre.function.FunHelper.logFun;

public class InlineEasingFluxDecorator<E, T extends Repeatable> {

    private final CacheManager cacheManager;
    private final Map<EAxisEaser, Function<Flux<T>, Flux<T>>> easerMap;
    private final Function<SceneDto, EAxisEaser> easerGetter;
    private final String REPEATER_CACHE_NAME = "repeater";
    private final BiFunction<SceneDto, String, String> CACHE_KEY = (q, p) -> "EASER_SCENE_%s_TRIGGER_%s".formatted(q.getName(), p);

    private final Sinks.Many<SceneDto> sceneSink = Sinks.many().unicast().onBackpressureBuffer();
    private final Sinks.Many<T> outputSink = Sinks.many().unicast().onBackpressureBuffer();

    private final String triggerName;

    public InlineEasingFluxDecorator(CacheManager cacheManager,
                                     Flux<T> sourceFlux,
                                     Map<EAxisEaser, Function<Flux<T>, Flux<T>>> easerMap,
                                     Function<SceneDto, EAxisEaser> easerGetter,
                                     Map<E, Consumer<T>> consumerMap,
                                     SerializableFunction<SceneDto, E> axisActionGetter) {
        this.cacheManager = cacheManager;
        this.easerMap = easerMap;
        this.easerGetter = easerGetter;

        triggerName = extractMethodName(axisActionGetter);
        sceneSink.asFlux()
                .map(this::getCachedOrFreshEaser)
                .switchMap(repeaterDef ->
                        Optional.ofNullable(Optional.ofNullable(repeaterDef.repeater()).orElseGet(Function::identity)
                                        .apply(sourceFlux)).orElseGet(Flux::empty)
                                .mapNotNull(
                                        funky(axisActionGetter
                                                .andThen(q -> consumerMap.getOrDefault(q, outputSink::tryEmitNext))
                                                .apply(repeaterDef.scene))
                                )
                )
                .subscribe();
    }

    public void setScene(SceneDto scene) {
        sceneSink.tryEmitNext(scene);
    }

    public Flux<T> getRepeatingStream() {
        return outputSink
                .asFlux()
                ;
    }

    SceneAndRepeater<T> getCachedOrFreshEaser(SceneDto scene) {
        return tryGetCachedEaser(CACHE_KEY.apply(scene, triggerName)) instanceof SceneAndRepeater<T> c ?
                    c : getEaserAndCache(scene);
    }

    private SceneAndRepeater<T> tryGetCachedEaser(String key) {
        return cacheManager.getCache(REPEATER_CACHE_NAME).get(key, SceneAndRepeater.class);
    }

    private Function<String, Consumer<SceneAndRepeater<T>>> cache(CacheManager cacheManager) {
        return q -> p -> Optional.ofNullable(cacheManager.getCache(REPEATER_CACHE_NAME))
                .ifPresent(c -> c.put(q, p));
    }

    private SceneAndRepeater<T> getEaserAndCache(SceneDto scene) {
        return easerGetter
                .andThen(funky(logFun("getting easer name: '{}'")))
                .andThen(easerMap::get)
                .andThen(createCacheRecord(scene))
                .andThen(funky(cache(cacheManager).apply(CACHE_KEY.apply(scene, triggerName))))
                .apply(scene);
    }

    Function<Function<Flux<T>, Flux<T>>, SceneAndRepeater<T>> createCacheRecord(SceneDto scene) {
        return q -> new SceneAndRepeater<>(scene, q);
    }

    private record SceneAndRepeater<T>(SceneDto scene, Function<Flux<T>, Flux<T>> repeater) {
    }
}