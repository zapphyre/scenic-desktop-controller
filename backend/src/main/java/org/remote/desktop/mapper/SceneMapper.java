package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.service.GPadEventStreamService;
import org.remote.desktop.util.RecursiveScraper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Mapper(componentModel = "spring", uses = GamepadEventMapper.class)
public interface SceneMapper {

    RecursiveScraper<GamepadEvent, Scene> scraper = new RecursiveScraper<>();

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
        return scraper.scrapeActionsRecursive(entity);
    }

    default Consumer<Scene> update(SceneVto source, Scene inherits) {
        return q -> update(q, source, inherits);
    }

    @Mapping(target = "inherits", ignore = true)
    @Mapping(target = "gamepadEvents", ignore = true)
    void update(@MappingTarget Scene target, SceneVto source, @Context Scene inherits);

    @Mapping(target = "inherits", ignore = true)
    @Mapping(target = "gamepadEvents", ignore = true)
    Scene map(SceneVto vto, @Context Scene inherits);

    @AfterMapping
    default void afterUpdate(@MappingTarget Scene target, @Context Scene inherits) {
        target.setInherits(inherits);
    }

    default Function<SceneVto, Scene> mapToEntity(Scene inherits) {
        return q -> map(q, inherits);
    }
}
