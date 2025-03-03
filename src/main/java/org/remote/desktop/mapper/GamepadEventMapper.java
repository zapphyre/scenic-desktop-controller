package org.remote.desktop.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.model.dto.GamepadEventDto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring")
public interface GamepadEventMapper {

    GamepadEventDto map(GamepadEvent GamepadEvent, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "scene", ignore = true)
    GamepadEvent map(GamepadEventDto vto, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "scene", ignore = true)
    void update(GamepadEventDto src, @MappingTarget GamepadEvent tgt, @Context CycleAvoidingMappingContext ctx);

    default Consumer<GamepadEvent> updater(GamepadEventDto src) {
        return q -> update(src, q, new CycleAvoidingMappingContext());
    }

}
