package org.remote.desktop.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.model.vto.GamepadEventVto;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Mapper(componentModel = "spring", uses = XdoActionMapper.class)
public interface GamepadEventMapper {

    GamepadEventDto map(GamepadEvent GamepadEvent, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "scene", ignore = true)
    GamepadEvent map(GamepadEventDto vto, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "scene", ignore = true)
    void update(GamepadEventDto src, @MappingTarget GamepadEvent tgt, @Context CycleAvoidingMappingContext ctx);

    default Consumer<GamepadEvent> updater(GamepadEventDto src) {
        return q -> update(src, q, new CycleAvoidingMappingContext());
    }

    @Mapping(target = "nextSceneFk", source = "nextScene.id")
    @Mapping(target = "parentFk", source = "scene.id")
    GamepadEventVto map(GamepadEvent evt);

    List<GamepadEventVto> map(List<GamepadEvent> events);

    @Mapping(target = "id", source = "src.id")
    void update(@MappingTarget GamepadEvent tgt, GamepadEventVto src, Scene scene, Scene nextScene);

    default Consumer<GamepadEvent> update(GamepadEventVto src, Scene parent, Scene next) {
        return q -> update(q, src, parent, next);
    }

    @Mapping(target = "id", source = "vto.id")
    GamepadEvent map(GamepadEventVto vto, Scene scene, Scene nextScene);

    default Function<GamepadEventVto, GamepadEvent> map(Scene parent, Scene next) {
        return q -> map(q, parent, next);
    }
}
