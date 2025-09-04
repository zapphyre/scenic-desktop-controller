package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mode.model.EMode;
import org.remote.desktop.model.EAnalogControl;
import org.remote.desktop.model.event.ModeEvent;
import org.remote.desktop.model.event.select.AnalogControllerSelectEvent;
import org.remote.desktop.ui.select.UnoSelectApplication;
import org.remote.desktop.ui.select.mode.ModeSelector;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ModeSelectConfig {

    private final ApplicationEventPublisher eventPublisher;

    @Bean
    public ModeSelector createModeSelectApplication() {
        List<EMode> items = Arrays.asList(EMode.values());

        ModeSelector unoSelectApplication = new ModeSelector();

        unoSelectApplication.setItems(items, Enum::name)
                .update(q -> eventPublisher.publishEvent(
                        new ModeEvent(this, q.getAnalogControl().name())
                ));

        return unoSelectApplication;
    }
}
