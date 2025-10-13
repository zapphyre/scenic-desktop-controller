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

            CachedButtonActionDef bad = Arrays.stream(nnPrms)
                    .flatMap(only(ButtonActionDef.class))
                    .map(buttonPressMapper::mapCache)
                    .findFirst()
                    .orElse(null);

            GamepadDevice dev = Arrays.stream(nnPrms)
                    .flatMap(only(GamepadDevice.class))
                    .findFirst()
                    .orElseGet(() -> Arrays.stream(nnPrms)
                            .flatMap(only(GamepadDto.class))
                            .map(q -> new GamepadDevice(q.getName(), q.getDev()))
                            .findFirst()
                            .orElseGet(() -> Arrays.stream(nnPrms)
                                    .flatMap(only(ButtonActionDef.class))
                                    .findFirst()
                                    .map(ButtonActionDef::getDevice)
                                    .orElse(null)
                            )
                    );

            if (bad != null && dev != null)
                return new SimpleKey(bad, nnPrms, modeService.getCurrentModeNameFor(dev));

            if (dev != null)
                return new SimpleKey(nnPrms, modeService.getCurrentModeNameFor(dev));

            if (bad != null)
                return new SimpleKey(bad, nnPrms);

            return new SimpleKey(nnPrms);
        };
    }

    <T> Function<Object, Stream<T>> only(Class<T> clazz) {
        return q -> clazz.isInstance(q) ?
                Stream.of(clazz.cast(q)) : Stream.empty();
    }
}
