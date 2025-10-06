package org.remote.desktop.mapper;

import org.asmus.model.GamepadDevice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.remote.desktop.db.entity.Gamepad;
import org.remote.desktop.db.entity.GamepadMode;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.model.dto.GamepadModeDto;

@Mapper(componentModel = "spring")
public interface GamepadMapper {

    GamepadDto map(Gamepad gamepad);

    GamepadModeDto map(GamepadMode gamepadMode);

    @Mapping(target = "id", ignore = true)
    Gamepad map(GamepadDevice  gamepadDevice);

    GamepadDevice map(GamepadDto gamepadDto);
}
