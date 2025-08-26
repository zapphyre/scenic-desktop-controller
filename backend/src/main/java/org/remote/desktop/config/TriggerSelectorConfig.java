package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.ui.trigger.EaserSelectable;
import org.remote.desktop.ui.trigger.TriggerSelectApplication;
import org.remote.desktop.ui.trigger.TriggerSelectable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class TriggerSelectorConfig {

    @Bean
    public TriggerSelectApplication create() {
        List<TriggerSelectable> left = Arrays.stream(EAxisEvent.values())
                .map(TriggerSelectable::new)
                .toList();

        List<EaserSelectable> right = Arrays.stream(EAxisEaser.values())
                .map(EaserSelectable::new)
                .toList();

        TriggerSelectApplication triggerSelectApplication = new TriggerSelectApplication(left, right);

        triggerSelectApplication.setItems(left, right)
                .selected(q -> {

                });

        return triggerSelectApplication;
    }
}
