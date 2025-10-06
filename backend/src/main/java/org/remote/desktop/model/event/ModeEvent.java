package org.remote.desktop.model.event;

import lombok.Value;
import org.asmus.model.GamepadDevice;
import org.springframework.context.ApplicationEvent;

@Value
public class ModeEvent extends ApplicationEvent {

    String mode;
    GamepadDevice  device;

    public ModeEvent(Object source, String mode, GamepadDevice device) {
        super(source);
        this.mode = mode;
        this.device = device;
    }
}
