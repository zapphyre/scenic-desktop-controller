package org.remote.desktop.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.remote.desktop.entity.Scene;
import org.remote.desktop.model.SceneVto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SceneMapper {

    SceneVto map(Scene scene);

    Scene map(SceneVto sceneVto);

    SceneVto update(SceneVto source, @MappingTarget SceneVto target);

    List<SceneVto> mapToVdos(List<Scene> scenes, @Context CycleAvoidingMappingContext ctx);

    List<Scene> mapToEntities(List<SceneVto> scenes, @Context CycleAvoidingMappingContext ctx);
}
