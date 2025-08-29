package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.ui.select.trigger.TriggerSelectApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TriggerSelectorConfig {

    private final SceneDao sceneDao;

    @Bean
    public TriggerSelectApplication createTriggerSelectApplication() {
        List<EAxisEaser> items = Arrays.stream(EAxisEaser.values())
                .toList();

        TriggerSelectApplication triggerSelectApplication = new TriggerSelectApplication();

        triggerSelectApplication.setItems(items, Enum::name)
                .update(q -> {

                    System.out.println("TriggerSelectApplication updated " + q);
                });

        return triggerSelectApplication;
    }
}
