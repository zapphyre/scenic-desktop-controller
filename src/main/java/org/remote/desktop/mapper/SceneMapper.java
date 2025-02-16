package org.remote.desktop.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.remote.desktop.entity.Scene;
import org.remote.desktop.model.SceneVto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SceneMapper {

    SceneVto map(Scene sceneVto, @Context CycleAvoidingMappingContext ctx);

    Scene map(SceneVto sceneVto, @Context CycleAvoidingMappingContext ctx);

    void update(Scene source, @MappingTarget SceneVto target, @Context CycleAvoidingMappingContext ctx);
}
