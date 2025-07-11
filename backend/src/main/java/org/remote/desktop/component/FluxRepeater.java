package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.ui.model.StreamRepeaterDef;
import org.springframework.cache.CacheManager;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.util.FluxUtil.chew;
import static org.remote.desktop.util.FluxUtil.funky;

@RequiredArgsConstructor
public class FluxRepeater<T> {

    private final CacheManager cacheManager;
    private final Flux<T> workingOn;
    private final Map<EAxisEaser, Function<Flux<T>, Flux<T>>> repeaterMap;
    private final Function<SceneDto, EAxisEaser> repeatGetter;
    private final UUID uuid = UUID.randomUUID();

    private final Sinks.Many<T> flux = Sinks.many().multicast().directBestEffort();
    private Disposable disposable;

    public Consumer<String> forKeyOnScene(Function<String, SceneDto> sceneGetter, Consumer<SceneDto> currentScene) {
        return chew(sceneGetter.andThen(funky(currentScene)).andThen(getCachedOrFreshConsumers()), this::constructEasedPipeline);
    }

    void constructEasedPipeline(StreamRepeaterDef<T> easingFluxEventDef) {
        Optional.ofNullable(disposable).ifPresent(Disposable::dispose);

        disposable = easingFluxEventDef.repeater().apply(workingOn)
                .subscribe(flux::tryEmitNext);
    }

    Function<SceneDto, StreamRepeaterDef<T>> getCachedOrFreshConsumers() {
        return q -> getAxisCachedConsumers(q.getName()) instanceof StreamRepeaterDef<T> c ?
                c : getConsumersAndCache(q);
    }

    StreamRepeaterDef<T> getAxisCachedConsumers(String key) {
        return cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(key + uuid.toString(), StreamRepeaterDef.class);
    }

    StreamRepeaterDef<T> getConsumersAndCache(SceneDto sceneDto) {
        EAxisEaser repeater = repeatGetter.apply(sceneDto);

        StreamRepeaterDef<T> tStreamRepeaterDef = new StreamRepeaterDef<>(repeaterMap.get(repeater));

        cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME)
                .put(sceneDto.getName() + uuid.toString(), tStreamRepeaterDef);

        return tStreamRepeaterDef;
    }

    public Flux<T> getRepeatingStream() {
        return flux.asFlux().publish().refCount(1);
    }
}
