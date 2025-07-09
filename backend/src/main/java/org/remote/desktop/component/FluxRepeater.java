package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import org.asmus.model.PolarCoords;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.pojo.KeyPart;
import org.remote.desktop.ui.model.EasingFluxEventDef;
import org.remote.desktop.ui.model.StreamRepeaterDef;
import org.remote.desktop.util.FluxUtil;
import org.springframework.cache.CacheManager;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.remote.desktop.model.EAxisEaser.CONTINUOUS;
import static org.remote.desktop.util.FluxUtil.chew;

@RequiredArgsConstructor
public class FluxRepeater<T> {

    private final CacheManager cacheManager;
    private final Flux<T> workingOn;
    private final Map<EAxisEaser, Function<Flux<T>, Flux<T>>> repeaterMap;
    private final Function<SceneDto, EAxisEaser> repeatGetter;

    private final Sinks.Many<T> flux = Sinks.many().multicast().directBestEffort();


    public Consumer<String> forKeyOnScene(Function<String, SceneDto> sceneGetter) {
        return chew(sceneGetter.andThen(getCachedOrFreshConsumers()), this::constructEasedPipeline);
    }


    void constructEasedPipeline(StreamRepeaterDef<T> easingFluxEventDef) {
        easingFluxEventDef.repeater().apply(workingOn)
                .subscribe(flux::tryEmitNext);
    }

    Function<SceneDto, StreamRepeaterDef<T>> getCachedOrFreshConsumers() {
        return q -> getAxisCachedConsumers(q.getName()) instanceof StreamRepeaterDef<T> c ?
                c : getConsumersAndCache(q);
    }

    StreamRepeaterDef<T> getAxisCachedConsumers(String key) {
        return cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(key, StreamRepeaterDef.class);
    }


    StreamRepeaterDef<T> getConsumersAndCache(SceneDto sceneDto) {
        EAxisEaser repeater = repeatGetter.apply(sceneDto);

        StreamRepeaterDef<T> tStreamRepeaterDef = new StreamRepeaterDef<>(repeaterMap.get(repeater));

        cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME)
                .put(sceneDto.getName(), tStreamRepeaterDef);

        return tStreamRepeaterDef;
    }

    public Flux<T> getRepeatingStream() {
        return flux.asFlux();
    }
}
