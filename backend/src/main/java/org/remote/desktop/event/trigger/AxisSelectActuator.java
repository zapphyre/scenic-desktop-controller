package org.remote.desktop.event.trigger;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.select.AxisSelectEvent;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.ui.select.axis.AxisSelectApplication;
import org.remote.desktop.ui.select.trigger.TriggerSelectApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AxisSelectActuator implements ApplicationListener<AxisSelectEvent> {

    private final AxisSelectApplication axisSelectApplication;
    private final TriggerSelectApplication  triggerSelectApplication;
    private final XdoSceneService xdoSceneService;
    private final SceneService sceneService;


    @Override
    public void onApplicationEvent(AxisSelectEvent event) {
        if (event.isOn())
            triggerSelectApplication.render(xdoSceneService.getLastScene(), event.getEvent().getTrigger());

        xdoSceneService.forceScene(sceneService.getSystemScene());
    }
}
