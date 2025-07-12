package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.util.RecursiveScraper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface SceneMapper {

    RecursiveScraper<Event, Scene> scraper = new RecursiveScraper<>();

    @Mapping(target = "inheritsFromSafe", ignore = true)
    @Mapping(target = "leftAxisEaser", defaultValue = "CONTINUOUS")
    @Mapping(target = "rightAxisEaser", defaultValue = "CONTINUOUS")
    @Mapping(target = "rightTriggerEaser", defaultValue = "CONTINUOUS")
    @Mapping(target = "leftTriggerEaser", defaultValue = "NONE")
    SceneDto map(Scene sceneVto, @Context CycleAvoidingMappingContext ctx);

    List<SceneDto> map(List<Scene> sceneVto, @Context CycleAvoidingMappingContext ctx);

    Scene map(SceneDto sceneDto, @Context CycleAvoidingMappingContext ctx);

    List<Scene> mapDtos(List<SceneDto> sceneDto, @Context CycleAvoidingMappingContext ctx);

    void update(SceneDto source, @MappingTarget Scene target, @Context CycleAvoidingMappingContext ctx);

    default Consumer<Scene> updater(SceneDto source) {
        return q -> update(source, q, new CycleAvoidingMappingContext());
    }

    @Named("inheritedEvents")
    default Set<Event> inheritedEvents(Scene entity) {
        return scraper.scrapeActionsRecursive(entity);
    }

    @Mapping(target = "inheritedGamepadEvents", source = ".", qualifiedByName = "inheritedEvents")
    @Mapping(target = "inheritsIdFk", source = "inheritsFrom", qualifiedByName = "mapInheritNames")
    SceneVto map(Scene entity);

    @Named("mapInheritNames")
    default Set<Long> mapInheritNames(Set<Scene> inherits) {
        return inherits.stream()
                .map(Scene::getId)
                .collect(Collectors.toSet());
    }


    default Consumer<Scene> update(SceneVto source, List<Scene> inherits) {
        return q -> update(q, source, inherits);
    }

    @Mapping(target = "inheritsFrom", ignore = true)
    @Mapping(target = "events", ignore = true)
    void update(@MappingTarget Scene target, SceneVto source, @Context List<Scene> inherits);

    @Mapping(target = "inheritsFrom", ignore = true)
    @Mapping(target = "events", ignore = true) // i'm setting them by id
    Scene map(SceneVto vto, @Context List<Scene> inherits);

    @AfterMapping
    default void afterUpdate(@MappingTarget Scene target, @Context List<Scene> inherits) {
        target.setInheritsFrom(new HashSet<>(inherits));
    }

    default Function<SceneVto, Scene> mapWithInherents(List<Scene> inherits) {
        return q -> map(q, inherits);
    }
}
