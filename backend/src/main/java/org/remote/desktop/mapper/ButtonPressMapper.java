package org.remote.desktop.mapper;

import org.asmus.model.GamepadEvent;
import org.mapstruct.*;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.service.GPadEventStreamService;

import java.util.Optional;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface ButtonPressMapper {

    @Mapping(target = "trigger", source = "type")
    ButtonActionDef map(GamepadEvent gamepadEvent);

    @Mapping(target = "qualified", ignore = true)
    ButtonActionDef map(GamepadEventDto vto);

    @Named("map")
    ActionMatch map(ButtonActionDef defs);

    @AfterMapping
    default void mapTrigger(@MappingTarget ButtonActionDef.ButtonActionDefBuilder def, GamepadEvent gamepadEvent) {
        def.trigger(Optional.ofNullable(gamepadEvent.getLogicalEventType())
                .map(Enum::name)
                .orElse(gamepadEvent.getType().name())
        );
    }

    @Mapping(target = "action", source = "vto", qualifiedByName = "map")
    GPadEventStreamService.SceneBtnActions map(String windowName, GamepadEventDto vto);

    @Mapping(target = "buttonTrigger", ignore = true)
    NextSceneXdoAction map(GPadEventStreamService.SceneBtnActions actions);

    default Function<GamepadEventDto, GPadEventStreamService.SceneBtnActions> map(String windowName) {
        return vto -> map(windowName, vto);
    }
}
