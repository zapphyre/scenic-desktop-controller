package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.GamepadEventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.service.GPadEventStreamService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Mapper(componentModel = "spring", uses = GamepadEventMapper.class)
public interface SceneMapper {

    SceneDto map(Scene sceneVto, @Context CycleAvoidingMappingContext ctx);

    List<SceneDto> map(List<Scene> sceneVto, @Context CycleAvoidingMappingContext ctx);

    Scene map(SceneDto sceneDto, @Context CycleAvoidingMappingContext ctx);

    List<Scene> mapDtos(List<SceneDto> sceneDto, @Context CycleAvoidingMappingContext ctx);

    void update(SceneDto source, @MappingTarget Scene target, @Context CycleAvoidingMappingContext ctx);

    default Consumer<Scene> updater(SceneDto source) {
        return q -> update(source, q, new CycleAvoidingMappingContext());
    }

    @Mapping(target = "inheritedGamepadEvents", source = ".", qualifiedByName = "inheritedEvents")
    @Mapping(target = "inheritsNameFk", source = "inherits.name")
    SceneVto map(Scene entity);

    @Named("inheritedEvents")
    default List<GamepadEvent> mapInherents(Scene entity) {
        return new GPadEventStreamService.RecursiveScraper<GamepadEvent, Scene>().scrapeActionsRecursive(entity);
    }

    @Mapping(target = "gamepadEvents", ignore = true)
    @Mapping(target = "name", source = "source.name")
    @Mapping(target = "inherits", source = "inherits")
    @Mapping(target = "windowName", source = "source.windowName")
    @Mapping(target = "leftAxisEvent", source = "source.leftAxisEvent")
    @Mapping(target = "rightAxisEvent", source = "source.rightAxisEvent")
    void update(@MappingTarget Scene target, SceneVto source, Scene inherits);

    default Consumer<Scene> update(SceneVto source, Scene inherits) {
        return q -> update(q, source, inherits);
    }

    @Mapping(target = "gamepadEvents", ignore = true)
    @Mapping(target = "name", source = "vto.name")
    @Mapping(target = "windowName", source = "vto.windowName")
    @Mapping(target = "leftAxisEvent", source = "vto.leftAxisEvent")
    @Mapping(target = "rightAxisEvent", source = "vto.rightAxisEvent")
    Scene map(SceneVto vto, Scene inherits);

    default Function<SceneVto, Scene> mapToEntity(Scene inherits) {
        return q -> map(q, inherits);
    }
}
