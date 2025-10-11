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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;

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
                    .filter(ButtonActionDef.class::isInstance)
                    .map(ButtonActionDef.class::cast)
                    .map(buttonPressMapper::mapCache)
                    .findFirst()
                    .orElse(null);

            GamepadDevice dev = Arrays.stream(nnPrms)
                    .filter(GamepadDevice.class::isInstance)
                    .map(GamepadDevice.class::cast)
                    .findFirst()
                    .orElseGet(() -> Arrays.stream(nnPrms)
                            .filter(GamepadDto.class::isInstance)
                            .map(GamepadDto.class::cast)
                            .map(q -> new GamepadDevice(q.getName(), q.getDev()))
                            .findFirst()
                            .orElse(null)
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

    @Bean
    public KeyGenerator eventSceneRelevancyCacheKeyGenerator() {
        return (target, method, params) -> {
            return null;
        };
    }
}
