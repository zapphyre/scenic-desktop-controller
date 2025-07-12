package org.remote.desktop.component;

import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class GrokFluxRepeater<T> {

    private final CacheManager cacheManager;
    private final Map<EAxisEaser, Function<Flux<T>, Flux<T>>> easerMap;
    private final Function<SceneDto, EAxisEaser> easerGetter;
    private final String REPEATER_CACHE_NAME = "repeater";
    private final Function<SceneDto, String> CACHE_KEY = q -> "EASER_" + q.getName() + UUID.randomUUID();

    private final Sinks.Many<SceneDto> sceneSink = Sinks.many().unicast().onBackpressureBuffer();
    private final Sinks.Many<T> outputSink = Sinks.many().unicast().onBackpressureBuffer();

    public GrokFluxRepeater(CacheManager cacheManager,
                            Flux<T> sourceFlux,
                            Map<EAxisEaser, Function<Flux<T>, Flux<T>>> easerMap,
                            Function<SceneDto, EAxisEaser> easerGetter,
                            Map<EAxisEvent, Consumer<T>> consumerMap,
                            Function<SceneDto, EAxisEvent> axisActionGetter) {
        this.cacheManager = cacheManager;
        this.easerMap = easerMap;
        this.easerGetter = easerGetter;

        // Set up the pipeline to switch flux transformations and consumers based on scene changes
        sceneSink.asFlux()
                .map(this::getCachedOrFreshEaser)
                .switchMap(repeaterDef -> repeaterDef.repeater()
                        .apply(sourceFlux)
                        .doOnNext(consumerMap.getOrDefault(axisActionGetter.apply(repeaterDef.scene()), q -> {
                            System.out.println(q);
                        }))
                )
                .subscribe(outputSink::tryEmitNext, outputSink::tryEmitError, outputSink::tryEmitComplete);
    }

    public void setScene(SceneDto scene) {
        sceneSink.tryEmitNext(scene);
    }

    public Flux<T> getRepeatingStream() {
        return outputSink
                .asFlux()
                .publish().autoConnect()
                ;
    }

    SceneAndRepeater<T> getCachedOrFreshEaser(SceneDto scene) {
//        return tryGetCachedEaser(scene.getName()) instanceof SceneAndRepeater<T> c ?
//                c : getEaserAndCache(scene);

        return getEaserAndCache(scene);
    }

    private SceneAndRepeater<T> tryGetCachedEaser(String key) {
        System.out.println("tryGetCachedEaser " + key);
        return cacheManager.getCache(REPEATER_CACHE_NAME).get(key, SceneAndRepeater.class);
    }

    private Function<String, Consumer<SceneAndRepeater<T>>> cache(CacheManager cacheManager) {
        return q -> p -> Optional.ofNullable(cacheManager.getCache(REPEATER_CACHE_NAME))
                .ifPresent(c -> c.put(q, p));
    }

    private SceneAndRepeater<T> getEaserAndCache(SceneDto scene) {
        return easerGetter
                .andThen(easerMap::get)
                .andThen(createCacheRecord(scene))
//                .andThen(funky(cache(cacheManager).apply(CACHE_KEY.apply(scene))))
                .apply(scene);
    }

    Function<Function<Flux<T>, Flux<T>>, SceneAndRepeater<T>> createCacheRecord(SceneDto scene) {
        return q -> new SceneAndRepeater<>(scene, q);
    }

    // Helper record to pair SceneDto with StreamRepeaterDef
    private record SceneAndRepeater<T>(SceneDto scene, Function<Flux<T>, Flux<T>> repeater) {
    }
}