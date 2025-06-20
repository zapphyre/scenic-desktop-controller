package org.remote.desktop.mapper;

import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.mapstruct.*;
import org.remote.desktop.model.*;
import org.remote.desktop.model.dto.ButtonEventDto;
import org.remote.desktop.model.dto.EventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.GPadEventStreamService;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface ButtonPressMapper {

    @Mapping(target = "logicalTrigger", ignore = true)
    @Mapping(target = "trigger", source = ".", qualifiedByName = "logicalTriggerName")
    ButtonActionDef map(GamepadEvent gamepadEvent);

    @Mapping(target = "buttonEvent", source = "source.buttonEvent")
//    @Mapping(target = "buttonEvent.modifiers", source = "modifiers")
    EventDto concatModifiers(EventDto source, Set<EButtonAxisMapping> modifiers);

    default EventDto concatModifiers(EventDto source,
                             Set<EButtonAxisMapping> modifiers, String newModifier) {
        Set<EButtonAxisMapping> mods = Stream.of(modifiers, Set.of(EButtonAxisMapping.valueOf(newModifier)))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        source.getButtonEvent().setModifiers(mods);

        return concatModifiers(source, mods);
    }

    @AfterMapping
    default ButtonActionDef map(@MappingTarget ButtonActionDef.ButtonActionDefBuilder buttonActionDef, GamepadEvent gamepadEvent) {
        ButtonActionDef def = buttonActionDef.build();

        try {
            def = def.withLogicalTrigger(ELogicalTrigger.valueOf(def.getTrigger()));
        } catch (IllegalArgumentException e) {

        }

        return def;
    }

    // logical trigger is EButtonAxisMapping.mapping__ELogicalEventType
    @Named("logicalTriggerName")
    default String logicalTriggerName(GamepadEvent gamepadEvent) {
        return Optional.ofNullable(gamepadEvent.getLogicalEventType())
                .map(Enum::name)
                .map(ExpandedLogicalNaming.withPrefix(gamepadEvent.getType().getMapping()))
                .orElseGet(() -> gamepadEvent.getType().name());
    }

    @Mapping(target = "qualified", ignore = true)
    ButtonActionDef map(ButtonEventDto vto);

    @Named("map")
    @Mapping(target = "multiplicity", defaultValue = "CLICK")
    ActionMatch map(ButtonActionDef defs);

    @Mapping(target = "action", source = "dto.buttonEvent", qualifiedByName = "map")
    @Mapping(target = "windowName", source = "currentScene.windowName")
    @Mapping(target = "eventSourceScene", source = "dto.scene")
    GPadEventStreamService.SceneBtnActions map(SceneDto currentScene, EventDto dto);

    @Mapping(target = "buttonTrigger", ignore = true)
    NextSceneXdoAction map(GPadEventStreamService.SceneBtnActions actions);

    default Function<EventDto, GPadEventStreamService.SceneBtnActions> map(SceneDto currentScene) {
        return dto -> map(currentScene, dto);
    }
}
