package org.remote.desktop.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.remote.desktop.entity.Scene;
import org.remote.desktop.model.SceneVto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SceneMapper {

    SceneVto map(Scene scene, @Context CycleAvoidingMappingContext ctx);

    Scene map(SceneVto sceneVto);

    List<SceneVto> mapToVdos(List<Scene> scenes, @Context CycleAvoidingMappingContext ctx);

    List<Scene> mapToEntities(List<SceneVto> scenes, @Context CycleAvoidingMappingContext ctx);
}
