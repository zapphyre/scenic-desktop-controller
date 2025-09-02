package org.remote.desktop.mode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.remote.desktop.mode.model.*;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.springframework.context.ApplicationEvent;

@NoArgsConstructor
@AllArgsConstructor
public class EventModeFactory {

    @Getter
    private Mode lastMode;

    private final XdoMode xdoMode = new XdoMode();
    private final WinderMode winderMode = new WinderMode();
    private final KeyboardMode keyboardMode = new KeyboardMode();
    private final AnalogControlSelectMode analogControlSelectMode = new AnalogControlSelectMode();
    private final LampMode lampMode = new LampMode();

    public Mode changeMode(EMode eMode) {
        return lastMode = switch (eMode) {
            case XDO -> xdoMode;
            case WINDER -> winderMode;
            case KEYBOARD -> keyboardMode;
            case TRIGGER_SELECT -> analogControlSelectMode;
            case LAMP -> lampMode;
        };
    }

    public ApplicationEvent createEvent(GpadCommandEvent e) {
        return lastMode.currentModeEvent(e);
    }
}
