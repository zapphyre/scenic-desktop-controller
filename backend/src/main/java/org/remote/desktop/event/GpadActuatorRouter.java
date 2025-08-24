package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mode.EventModeFactory;
import org.remote.desktop.mode.model.EMode;
import org.remote.desktop.mode.model.Mode;
import org.remote.desktop.mode.model.XdoMode;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GpadActuatorRouter implements ApplicationListener<GpadCommandEvent> {

    private final ApplicationEventPublisher eventPublisher;
    private final EventModeFactory modeFactory = new EventModeFactory(new XdoMode());

    @Override
    public void onApplicationEvent(GpadCommandEvent e) {
        Mode mode = switch (e.getKeyPart().getKeyEvt()) {
            case TRIGGER_ADJUST -> modeFactory.changeMode(EMode.TRIGGER_SELECT);
            case KEYBOARD_ON -> modeFactory.changeMode(EMode.KEYBOARD);
            case WINDER -> modeFactory.changeMode(EMode.WINDER);

            case SCENE_RESET -> modeFactory.changeMode(EMode.XDO);

            default -> modeFactory.getLastMode();
        };

        ApplicationEvent evt = mode.currentModeEvent(e);
        eventPublisher.publishEvent(evt);
    }
}
