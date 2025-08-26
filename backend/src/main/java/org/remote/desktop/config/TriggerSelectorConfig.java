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
    public TriggerSelectApplication<EAxisEvent, EAxisEaser> create() {
        List<EAxisEvent> left = Arrays.stream(EAxisEvent.values())
                .toList();

        List<EAxisEaser> right = Arrays.stream(EAxisEaser.values())
                .toList();

        TriggerSelectApplication<EAxisEvent, EAxisEaser> triggerSelectApplication = new TriggerSelectApplication<>(left, right);


        triggerSelectApplication.setItems(left, right, Enum::name, Enum::name)
                .selected(q -> {
                    System.out.println("Selected " + q);
                });

        return triggerSelectApplication;
    }
}
