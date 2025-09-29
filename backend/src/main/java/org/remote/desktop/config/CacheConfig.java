package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.service.impl.ModeService;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.stream.Stream;

@EnableCaching
@Configuration
@RequiredArgsConstructor
public class CacheConfig implements CachingConfigurer {

    private final ModeService  modeService;

    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            params = Stream.of(params)
                    .filter(Objects::nonNull)
                    .toArray(Object[]::new);

            if (params.length == 0)
                return new SimpleKey(modeService.getCurrentMode().getName());

            Object[] keyParts = new Object[params.length + 1];
            System.arraycopy(params, 0, keyParts, 0, params.length);

            keyParts[params.length] = modeService.getCurrentMode().getName();

            return new SimpleKey(keyParts);
        };
    }
    clickKeyGenerator
    @Bean
    public KeyGenerator clickKeyGenerator() { // b/c
        return (target, method, params) -> {
            ButtonActionDef click = params[0] instanceof ButtonActionDef c ? c : null; // Safe cast since click is never null

            if (click == null) return new SimpleKey("null-click");

            // Create key from click, its properties, and service state
            Object[] keyParts = new Object[] {
                    click.getTrigger(),
                    click.getModifiers(),
                    click.getMultiplicity(),
                    click.isLongPress(),
                    modeService.getCurrentMode().getName()
            };

            return new SimpleKey(keyParts);
        };
    }
}
