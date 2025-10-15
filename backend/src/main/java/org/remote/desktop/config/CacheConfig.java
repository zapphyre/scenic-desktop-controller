package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.CachedButtonActionDef;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.service.impl.ModeService;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@EnableCaching
@Configuration
@RequiredArgsConstructor
public class CacheConfig implements CachingConfigurer {

    private final ModeService modeService;
    private final ButtonPressMapper buttonPressMapper;

    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            Object[] nnPrms = Arrays.stream(params)
                    .filter(Objects::nonNull)
                    .toArray(Object[]::new);

            CachedButtonActionDef bad = firstOfType(ButtonActionDef.class, nnPrms)
                    .map(buttonPressMapper::mapCache)
                    .orElse(null);

            GamepadDevice dev = firstOfType(GamepadDevice.class, nnPrms)
                    .orElseGet(() -> firstOfType(GamepadDto.class, nnPrms)
                            .map(q -> new GamepadDevice(q.getName(), q.getDev()))
                            .orElseGet(() -> firstOfType(ButtonActionDef.class, nnPrms)
                                    .map(ButtonActionDef::getDevice)
                                    .orElse(null)));

//            return new SimpleKey(bad, nnPrms, modeService.getCurrentModeNameFor(dev));

            if (bad != null && dev != null)
                return new SimpleKey(bad, nnPrms, modeService.getCurrentModeNameFor(dev));

            if (dev != null)
                return new SimpleKey(nnPrms, modeService.getCurrentModeNameFor(dev));

            if (bad != null)
                return new SimpleKey(bad, nnPrms);

            return new SimpleKey(nnPrms);
        };
    }

    <T> Optional<T> firstOfType(Class<T> clazz, Object[] nnPrms) {
        return Arrays.stream(nnPrms)
                .flatMap(only(clazz))
                .findFirst();
    }

    <T> Function<Object, Stream<T>> only(Class<T> clazz) {
        return q -> clazz.isInstance(q) ?
                Stream.of(clazz.cast(q)) : Stream.empty();
    }
}
