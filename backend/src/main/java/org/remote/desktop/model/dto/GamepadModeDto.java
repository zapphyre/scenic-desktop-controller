package org.remote.desktop.model.dto;

import lombok.Value;
import org.remote.desktop.db.entity.Gamepad;

@Value
public class GamepadModeDto {

    Long id;
    Gamepad device;
    String mode;
}
