package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.ui.select.axis.AxisSelectApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AxisSelectorConfig {

    private final SceneDao sceneDao;

    @Bean
    public AxisSelectApplication<EAxisEvent, EAxisEaser> create() {
        List<EAxisEvent> left = Arrays.stream(EAxisEvent.values())
                .toList();

        List<EAxisEaser> right = Arrays.stream(EAxisEaser.values())
                .toList();

        AxisSelectApplication<EAxisEvent, EAxisEaser> axisSelectApplication = new AxisSelectApplication<>();

        axisSelectApplication.setItems(left, right, Enum::name, Enum::name)
                .selected((update) -> {

                    Optional.of(update.getSceneDto())
                            .map(q -> switch (update.getTrigger()) {
                                case "TRIGGER_LEFT" -> update.getTrigger();
                                default -> update.getTrigger();
                            });

//                    sceneDao.update();
                });

        return axisSelectApplication;
    }
}
