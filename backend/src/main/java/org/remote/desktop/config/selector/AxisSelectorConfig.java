package org.remote.desktop.config.selector;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.ETriggerEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.StateService;
import org.remote.desktop.service.impl.SceneManager;
import org.remote.desktop.ui.select.axis.AxisUiSelector;
import org.remote.desktop.ui.select.trigger.TriggerUiSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AxisSelectorConfig {

    private final SceneService sceneDao;
    private final StateService stateService;
    private final SceneManager xdoSceneService;

    @Bean
    @Primary
    public AxisUiSelector axisSelector() {
        List<EAxisEvent> left = Arrays.asList(EAxisEvent.values());
        List<EAxisEaser> right = Arrays.asList(EAxisEaser.values());

        AxisUiSelector duoSelectApplication = new AxisUiSelector();

        duoSelectApplication.setItems(left, right, Enum::name, Enum::name)
                .selected(update -> {

                    SceneDto updated = switch (update.getTrigger()) {
                        case LEFT_STICK ->
                                update.getSceneDto().withLeftAxisEvent(update.getLeft()).withLeftAxisEaser(update.getRight());
                        case RIGHT_STICK ->
                                update.getSceneDto().withRightAxisEvent(update.getLeft()).withRightAxisEaser(update.getRight());
                        default -> throw new IllegalStateException("Unexpected value: " + update.getTrigger());
                    };

                    sceneDao.update(updated);
                    duoSelectApplication.close();
                });

        return duoSelectApplication;
    }

    @Bean
    public TriggerUiSelector triggerSelector() {
        List<ETriggerEvent> left = Arrays.asList(ETriggerEvent.values());
        List<EAxisEaser> right = Arrays.asList(EAxisEaser.values());

        TriggerUiSelector duoSelectApplication = new TriggerUiSelector();

        duoSelectApplication.setItems(left, right, Enum::name, Enum::name)
                .selected(update -> {

                    SceneDto updated = switch (update.getTrigger()) {
                        case LEFT_TRIGGER -> update.getSceneDto()
                                .withLeftTriggerEvent(update.getLeft())
                                .withLeftTriggerEaser(update.getRight());
                        case RIGHT_TRIGGER -> update.getSceneDto()
                                .withRightTriggerEvent(update.getLeft())
                                .withRightTriggerEaser(update.getRight());
                        default -> throw new IllegalStateException("Unexpected value: " + update.getTrigger());
                    };

                    sceneDao.update(updated);
                    duoSelectApplication.close();
                    stateService.nullifyForced();
                    xdoSceneService.tryGetCurrentName();
                });

        return duoSelectApplication;
    }
}
