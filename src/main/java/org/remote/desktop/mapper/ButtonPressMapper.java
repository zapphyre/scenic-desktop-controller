package org.remote.desktop.mapper;

import org.asmus.model.GamepadEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.vto.GPadEventVto;
import org.remote.desktop.service.GPadEventStreamService;

import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface ButtonPressMapper {

    @Mapping(target = "trigger", source = "type")
    ButtonActionDef map(GamepadEvent gamepadEvent);

    @Named("map")
    ButtonActionDef map(GPadEventVto vto);

    @Mapping(target = "buttonActionDef", source = "vto", qualifiedByName = "map")
    GPadEventStreamService.SceneBtnActions map(String windowName, GPadEventVto vto);

    default Function<GPadEventVto, GPadEventStreamService.SceneBtnActions> map(String windowName) {
        return vto -> map(windowName, vto);
    }
}
