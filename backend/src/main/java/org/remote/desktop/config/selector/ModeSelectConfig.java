package org.remote.desktop.config.selector;

import lombok.RequiredArgsConstructor;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.model.event.ModeEvent;
import org.remote.desktop.ui.select.mode.ModeSelector;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class ModeSelectConfig {

    private final Map<String, GpadOsActionModule> actuatorModules;
    private final ApplicationEventPublisher eventPublisher;

    @Bean
    public ModeSelector createModeSelectApplication() {
        ModeSelector unoSelectApplication = new ModeSelector();

        unoSelectApplication.setItems(actuatorModules.keySet().stream().toList(), Function.identity())
                .update(q -> eventPublisher.publishEvent(
                        new ModeEvent(this, q.getElement() , q.getDevice())
                ));

        return unoSelectApplication;
    }
}
