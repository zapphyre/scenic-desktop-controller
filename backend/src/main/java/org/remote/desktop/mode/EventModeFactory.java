package org.remote.desktop.mode;

import lombok.*;
import org.remote.desktop.mode.model.*;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.springframework.context.ApplicationEvent;

@NoArgsConstructor
@AllArgsConstructor
public class EventModeFactory {

    @Getter
    private Mode lastMode;

    public Mode changeMode(EMode eMode) {
        return lastMode = switch (eMode) {
            case XDO -> new XdoMode();
            case WINDER -> new WinderMode();
            case KEYBOARD -> new KeyboardMode();
            case TRIGGER_SELECT -> new TriggerSelectMode();
        };
    }

    public ApplicationEvent createEvent(GpadCommandEvent e) {
        return lastMode.currentModeEvent(e);
    }
}
