package org.remote.desktop.event;

import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class VirtualInputStateRepository implements ApplicationListener<XdoCommandEvent> {

    private final AtomicBoolean active = new AtomicBoolean(false);

    public boolean isActive() {
        return active.get();
    }

    @Override
    public void onApplicationEvent(XdoCommandEvent e) {
        if (e.getKeyPart().getKeyEvt() == EKeyEvt.KEYBOARD_ON)
            active.set(true);

        if (e.getKeyPart().getKeyEvt() == EKeyEvt.KEYBOARD_OFF)
            active.set(false);
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
