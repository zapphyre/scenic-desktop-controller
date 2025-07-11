package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.ui.model.StreamRepeaterDef;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class GrokFluxRepeater<T> {

    private final CacheManager cacheManager;
    private final Flux<T> sourceFlux;
    private final Map<EAxisEaser, Function<Flux<T>, Flux<T>>> easerMap;
    private final Function<SceneDto, EAxisEaser> easerGetter;
    private final Map<EAxisEvent, Consumer<T>> consumerMap;
    private final Function<SceneDto, EAxisEvent> consumerGetter;
    private final UUID uuid = UUID.randomUUID();

    private final Sinks.Many<SceneDto> sceneSink = Sinks.many().unicast().onBackpressureBuffer();
    private final Sinks.Many<T> outputSink = Sinks.many().unicast().onBackpressureBuffer();

    public GrokFluxRepeater(CacheManager cacheManager, Flux<T> sourceFlux,
                        Map<EAxisEaser, Function<Flux<T>, Flux<T>>> easerMap,
                        Function<SceneDto, EAxisEaser> easerGetter,
                        Map<EAxisEvent, Consumer<T>> consumerMap,
                        Function<SceneDto, EAxisEvent> consumerGetter) {
        this.cacheManager = cacheManager;
        this.sourceFlux = sourceFlux;
        this.easerMap = easerMap;
        this.easerGetter = easerGetter;
        this.consumerMap = consumerMap;
        this.consumerGetter = consumerGetter;

        // Set up the pipeline to switch flux transformations and consumers based on scene changes
        sceneSink.asFlux()
                .map(scene -> new SceneAndRepeater<>(scene, getCachedOrFreshConsumers(scene)))
                .switchMap(sceneAndRepeater -> {
                    Function<Flux<T>, Flux<T>> transformer = sceneAndRepeater.repeater().repeater();
                    Consumer<T> consumer = consumerMap.get(consumerGetter.apply(sceneAndRepeater.scene()));
                    return transformer.apply(sourceFlux)
                            .doOnNext(consumer);
                })
                .subscribe(outputSink::tryEmitNext, outputSink::tryEmitError, outputSink::tryEmitComplete);
    }

    public void setScene(SceneDto scene) {
        sceneSink.tryEmitNext(scene);
    }

    public Flux<T> getRepeatingStream() {
        return outputSink.asFlux();
    }

    private StreamRepeaterDef<T> getCachedOrFreshConsumers(SceneDto scene) {
        String cacheKey = scene.getName() + uuid.toString();
        StreamRepeaterDef<T> cached = getAxisCachedConsumers(cacheKey);
        return cached != null ? cached : getConsumersAndCache(scene);
    }

    private StreamRepeaterDef<T> getAxisCachedConsumers(String key) {
        return cacheManager.getCache("sceneAxisCache").get(key, StreamRepeaterDef.class);
    }

    private StreamRepeaterDef<T> getConsumersAndCache(SceneDto scene) {
        EAxisEaser easer = easerGetter.apply(scene);
        StreamRepeaterDef<T> repeaterDef = new StreamRepeaterDef<>(easerMap.get(easer));
        cacheManager.getCache("sceneAxisCache").put(scene.getName() + uuid.toString(), repeaterDef);
        return repeaterDef;
    }

    // Helper record to pair SceneDto with StreamRepeaterDef
    private record SceneAndRepeater<T>(SceneDto scene, StreamRepeaterDef<T> repeater) {
    }
}