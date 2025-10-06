package org.remote.desktop.event.trigger;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EAnalogControl;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.event.select.AnalogControllerSelectEvent;
import org.remote.desktop.model.event.select.UiAnalogAdjustEvent;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.ui.select.UnoSelectApplication;
import org.remote.desktop.ui.select.axis.AxisUiSelector;
import org.remote.desktop.ui.select.trigger.TriggerUiSelector;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UiAnalogAdjustActuator implements ApplicationListener<UiAnalogAdjustEvent> {

    private final TriggerUiSelector triggerSelector;
    private final AxisUiSelector axisSelector;

    private final UnoSelectApplication<EAnalogControl> unoSelectApplication;

    private final XdoSceneService xdoSceneService;
    private final SceneService sceneService;

    private SceneDto adjustScene;

    @Override
    public void onApplicationEvent(UiAnalogAdjustEvent event) {
        adjustScene = xdoSceneService.getLastRecognizedScene();

        unoSelectApplication.setTitle(adjustScene.getName());
        unoSelectApplication.render(
                sceneService.getScene(event.getEvent().getRecognizedSceneName()), event.getEvent().getTrigger(), event.getEvent().getDevice()
        );
    }

    @Component
    class AnalogAdjustActuator implements ApplicationListener<AnalogControllerSelectEvent> {

        @Override
        public void onApplicationEvent(AnalogControllerSelectEvent event) {
            unoSelectApplication.close();

            switch (event.getAnalogControl()) {
                case LEFT_STICK:
                    axisSelector.render(adjustScene, event.getAnalogControl(), adjustScene.getLeftAxisEvent(), adjustScene.getLeftAxisEaser());
                    break;
                case RIGHT_STICK:
                    axisSelector.render(adjustScene, event.getAnalogControl(), adjustScene.getRightAxisEvent(), adjustScene.getRightAxisEaser());
                    break;
                case LEFT_TRIGGER:
                    triggerSelector.render(adjustScene, event.getAnalogControl(), adjustScene.getLeftTriggerEvent(), adjustScene.getLeftTriggerEaser());
                    break;
                case RIGHT_TRIGGER:
                    triggerSelector.render(adjustScene, event.getAnalogControl(), adjustScene.getRightTriggerEvent(), adjustScene.getRightTriggerEaser());
                    break;
            }
        }
    }
}
