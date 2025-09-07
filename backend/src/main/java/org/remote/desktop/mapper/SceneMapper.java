package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.Mode;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.util.RecursiveScraper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface SceneMapper {

    RecursiveScraper<Event, Scene> scraper = new RecursiveScraper<>();

    @Mapping(target = "leftAxisEaser", source = "leftAxisEaser", defaultValue = "CONTINUOUS")
    @Mapping(target = "rightAxisEaser", source = "rightAxisEaser", defaultValue = "CONTINUOUS")
    @Mapping(target = "rightTriggerEaser", source = "rightTriggerEaser", defaultValue = "CONTINUOUS")
    @Mapping(target = "leftTriggerEaser", source = "leftTriggerEaser", defaultValue = "NONE")
    @Mapping(target = "leftTriggerEvent", defaultValue = "DEFINED")
    @Mapping(target = "rightTriggerEvent", defaultValue = "DEFINED")
    SceneDto map(Scene entity, @Context CycleAvoidingMappingContext ctx);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "dto.id")
    Scene map(SceneDto dto, @Context Mode mode, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "mode", ignore = true)
    Scene map(SceneVto dto, @Context CycleAvoidingMappingContext ctx);

    List<SceneDto> map(List<Scene> entity, @Context CycleAvoidingMappingContext ctx);

    @Named("inheritedEvents")
    default Set<Event> inheritedEvents(Scene entity) {
        return scraper.scrapeActionsRecursive(entity);
    }

    @Mapping(target = "inheritedGamepadEvents", source = ".", qualifiedByName = "inheritedEvents")
    @Mapping(target = "inheritsIdFk", source = "inheritsFrom", qualifiedByName = "mapInheritNames")
    @Mapping(target = "mode", source = "mode.adapterMode")
    SceneVto map(Scene entity);

    @Named("mapInheritNames")
    default Set<Long> mapInheritNames(Set<Scene> inherits) {
        return inherits.stream()
                .map(Scene::getId)
                .collect(Collectors.toSet());
    }

    default Consumer<Scene> update(SceneVto source, List<Scene> inherits) {
        return q -> update(q, source, inherits, new CycleAvoidingMappingContext());
    }

    default Consumer<Scene> update(SceneDto source) {
        return q -> update(q, source, new CycleAvoidingMappingContext());
    }

    @Mapping(target = "inheritsFrom", ignore = true)
    @Mapping(target = "events", ignore = true) // why is this ignored?? -- maybe b/c from the fe i just want to update scene as such and other relations would throw unmanaged
    @Mapping(target = "mode", ignore = true)
    void update(@MappingTarget Scene target, SceneVto source, @Context List<Scene> inherits, CycleAvoidingMappingContext ctx);

    @Mapping(target = "events", ignore = true) // why is this ignored?? -- maybe b/c from the fe i just want to update scene as such and other relations would throw unmanaged
    @Mapping(target = "mode", ignore = true)
    void update(@MappingTarget Scene target, SceneDto source, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "inheritsFrom", ignore = true)
    @Mapping(target = "events", ignore = true) // i'm setting them by id
    @Mapping(target = "mode", ignore = true)
    Scene map(SceneVto vto, @Context List<Scene> inherits);

    @AfterMapping
    default void afterUpdate(@MappingTarget Scene target, @Context List<Scene> inherits) {
        target.setInheritsFrom(new HashSet<>(inherits));
    }

    default Function<SceneVto, Scene> mapWithInherents(List<Scene> inherits) {
        return q -> map(q, inherits);
    }

    default Function<? super SceneDto, Scene> mapWithMode(Mode mode) {
        return q -> map(q, mode, new CycleAvoidingMappingContext());
    }
}
