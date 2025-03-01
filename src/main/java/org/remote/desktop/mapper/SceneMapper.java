package org.remote.desktop.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.model.dto.SceneDto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SceneMapper {

    SceneDto map(Scene sceneVto, @Context CycleAvoidingMappingContext ctx);

    Scene map(SceneDto sceneDto, @Context CycleAvoidingMappingContext ctx);

    void update(SceneDto source, @MappingTarget Scene target, @Context CycleAvoidingMappingContext ctx);

    default Consumer<Scene> updater(SceneDto source) {
        return q -> update(source, q, new CycleAvoidingMappingContext());
    }
}
