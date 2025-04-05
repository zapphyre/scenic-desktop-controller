package org.remote.desktop.mapper;

import org.asmus.model.GamepadEvent;
import org.mapstruct.*;
import org.remote.desktop.model.*;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.service.GPadEventStreamService;

import java.util.Optional;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface ButtonPressMapper {

    @Mapping(target = "trigger", source = ".", qualifiedByName = "logicalTriggerName")
    ButtonActionDef map(GamepadEvent gamepadEvent);

    @AfterMapping
    default ButtonActionDef map(@MappingTarget ButtonActionDef.ButtonActionDefBuilder buttonActionDef, GamepadEvent gamepadEvent) {
        ButtonActionDef def = buttonActionDef.build();

        try {
            def = def.withLogicalTrigger(ELogicalTrigger.valueOf(def.getTrigger()));
        } catch (IllegalArgumentException e) {

        }

        return def;
    }

    @Named("logicalTriggerName")
    default String logicalTriggerName(GamepadEvent gamepadEvent) {
        return Optional.ofNullable(gamepadEvent.getLogicalEventType())
                .map(Enum::name)
                .map(ExpandedLogicalNaming.withPrefix(gamepadEvent.getType().getMapping()))
                .orElseGet(() -> gamepadEvent.getType().name());
    }

    @Mapping(target = "qualified", ignore = true)
    ButtonActionDef map(GamepadEventDto vto);

    @Named("map")
    @Mapping(target = "multiplicity", defaultValue = "CLICK")
    ActionMatch map(ButtonActionDef defs);

    @Mapping(target = "action", source = "vto", qualifiedByName = "map")
    GPadEventStreamService.SceneBtnActions map(String windowName, GamepadEventDto vto);

    @Mapping(target = "buttonTrigger", ignore = true)
    NextSceneXdoAction map(GPadEventStreamService.SceneBtnActions actions);

    default Function<GamepadEventDto, GPadEventStreamService.SceneBtnActions> map(String windowName) {
        return dto ->   map(windowName, dto);
    }
}
