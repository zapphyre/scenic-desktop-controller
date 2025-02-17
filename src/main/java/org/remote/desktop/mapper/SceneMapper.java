package org.remote.desktop.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.remote.desktop.entity.Scene;
import org.remote.desktop.model.SceneVto;

import java.util.function.Consumer;
import java.util.function.Function;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SceneMapper {

    SceneVto map(Scene sceneVto, @Context CycleAvoidingMappingContext ctx);

    Scene map(SceneVto sceneVto, @Context CycleAvoidingMappingContext ctx);

    void update(SceneVto source, @MappingTarget Scene target, @Context CycleAvoidingMappingContext ctx);

    default Consumer<Scene> updater(SceneVto source) {
        return q -> update(source, q, new CycleAvoidingMappingContext());
    }
}
