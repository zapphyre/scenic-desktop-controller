package org.remote.desktop.mapper;

import org.asmus.model.GamepadEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.remote.desktop.model.ButtonActionDef;

@Mapper(componentModel = "spring")
public interface ButtonPressMapper {

    @Mapping(target = "trigger", source = "type")
    ButtonActionDef map(GamepadEvent gamepadEvent);
}
