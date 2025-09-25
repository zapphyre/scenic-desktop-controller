package org.remote.desktop.event.trigger;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EAnalogControl;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.event.select.AnalogControllerSelectEvent;
import org.remote.desktop.model.event.select.UiAnalogAdjustEvent;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.ui.select.SelectSelector;
import org.remote.desktop.ui.select.UnoSelectApplication;
import org.remote.desktop.ui.select.axis.AxisUiSelector;
import org.remote.desktop.ui.select.trigger.TriggerUiSelector;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UiAnalogAdjustActuator implements ApplicationListener<UiAnalogAdjustEvent> {

    private final TriggerUiSelector triggerSelector;
    private final SelectSelector  selectSelector;
    private final AxisUiSelector axisSelector;

    private final UnoSelectApplication<EAnalogControl> unoSelectApplication;

    private final XdoSceneService xdoSceneService;
    private final SceneService sceneService;

    SceneDto s;

    @Override
    public void onApplicationEvent(UiAnalogAdjustEvent event) {
        s = xdoSceneService.getLastRecognizedScene();
        unoSelectApplication.setTitle(s.getName());

        if (event.isOn())
            unoSelectApplication.render(
                    sceneService.getScene(event.getEvent().getRecognizedSceneName()), event.getEvent().getTrigger()
            );

        xdoSceneService.forceScene(sceneService.getSystemScene());
    }

    @Component
    class AnalogAdjustActuator implements ApplicationListener<AnalogControllerSelectEvent> {

        @Override
        public void onApplicationEvent(AnalogControllerSelectEvent event) {
            unoSelectApplication.close();

            System.out.println("adjusting scene name: " + s.getName());

            switch (event.getAnalogControl()) {
                case LEFT_STICK:
                    axisSelector.render(s, event.getAnalogControl(), s.getLeftAxisEvent(), s.getLeftAxisEaser());
                    break;
                case RIGHT_STICK:
                    axisSelector.render(s, event.getAnalogControl(), s.getRightAxisEvent(), s.getRightAxisEaser());
                    break;
                case LEFT_TRIGGER:
                    triggerSelector.render(s, event.getAnalogControl(), s.getLeftTriggerEvent(), s.getLeftTriggerEaser());
                    break;
                case RIGHT_TRIGGER:
                    triggerSelector.render(s, event.getAnalogControl(), s.getRightTriggerEvent(), s.getRightTriggerEaser());
                    break;
            }
        }
    }
}
