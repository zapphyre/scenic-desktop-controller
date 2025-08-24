package org.remote.desktop.event.trigger;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.trigger.TriggerSelectEvent;
import org.remote.desktop.ui.trigger.TriggerSelectApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TriggerSelectActuator implements ApplicationListener<TriggerSelectEvent> {

    private final TriggerSelectApplication triggerSelectApplication;


    @Override
    public void onApplicationEvent(TriggerSelectEvent event) {
        triggerSelectApplication.render();
    }
}
