package org.remote.desktop.mapper;

import org.asmus.model.EQualificationType;
import org.asmus.model.GamepadEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.vto.GPadEventVto;
import org.remote.desktop.service.GPadEventStreamService;

import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface ButtonPressMapper {

    @Mapping(target = "trigger", source = "type")
    ButtonActionDef map(GamepadEvent gamepadEvent);

    ButtonActionDef map(GPadEventVto vto);

    @Named("map")
    ActionMatch map(ButtonActionDef defs);

    @Mapping(target = "action", source = "vto", qualifiedByName = "map")
    GPadEventStreamService.SceneBtnActions map(String windowName, GPadEventVto vto);

    NextSceneXdoAction map(GPadEventStreamService.SceneBtnActions actions);

    default Function<GPadEventVto, GPadEventStreamService.SceneBtnActions> map(String windowName) {
        return vto -> map(windowName, vto);
    }
}
