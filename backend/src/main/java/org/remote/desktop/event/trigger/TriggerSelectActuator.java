package org.remote.desktop.event.trigger;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.trigger.TriggerSelectEvent;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.ui.trigger.TriggerSelectApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TriggerSelectActuator implements ApplicationListener<TriggerSelectEvent> {

    private final TriggerSelectApplication triggerSelectApplication;
    private final XdoSceneService xdoSceneService;
    private final SceneService sceneService;


    @Override
    public void onApplicationEvent(TriggerSelectEvent event) {
        EKeyEvt keyEvt = event.getEvent().getKeyPart().getKeyEvt();

        if (event.isOn())
            triggerSelectApplication.render();

        xdoSceneService.forceScene(sceneService.getSystemScene());

//        if (event.getEvent().getTrigger().equals("UP"))
//            triggerSelectApplication.
    }
}
