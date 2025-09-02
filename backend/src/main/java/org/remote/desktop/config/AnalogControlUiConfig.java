package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EAnalogControl;
import org.remote.desktop.model.event.select.AnalogControllerSelectEvent;
import org.remote.desktop.ui.select.UnoSelectApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnalogControlUiConfig {

    private final ApplicationEventPublisher eventPublisher;

    @Bean
    public UnoSelectApplication<EAnalogControl> createTriggerSelectApplication() {
        List<EAnalogControl> items = Arrays.asList(EAnalogControl.values());

        UnoSelectApplication<EAnalogControl> unoSelectApplication = new UnoSelectApplication<>();

        unoSelectApplication.setItems(items, Enum::name)
                .update(q -> eventPublisher.publishEvent(
                        new AnalogControllerSelectEvent(this, q.getAnalogControl(), q.getSceneDto()))
                );

        return unoSelectApplication;
    }
}
