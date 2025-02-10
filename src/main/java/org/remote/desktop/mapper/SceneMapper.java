package org.remote.desktop.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.remote.desktop.entity.Scene;
import org.remote.desktop.model.SceneVdo;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SceneMapper {

    SceneVdo map(Scene scene, @Context CycleAvoidingMappingContext ctx);

    Scene map(SceneVdo sceneVdo);

    List<SceneVdo> mapToVdos(List<Scene> scenes, @Context CycleAvoidingMappingContext ctx);

    List<Scene> mapToEntities(List<SceneVdo> scenes, @Context CycleAvoidingMappingContext ctx);
}
