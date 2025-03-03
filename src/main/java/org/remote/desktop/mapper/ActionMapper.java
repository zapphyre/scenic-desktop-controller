package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.model.dto.GamepadEventDto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring")
public interface ActionMapper {

    GamepadEventDto map(GamepadEvent GamepadEvent, @Context CycleAvoidingMappingContext ctx);

    GamepadEvent map(GamepadEventDto vto, @Context CycleAvoidingMappingContext ctx);

//    @Mapping(target = "", ignore = true)
    void update(GamepadEventDto src, @MappingTarget GamepadEvent tgt, @Context CycleAvoidingMappingContext ctx);

    default Consumer<GamepadEvent> updater(GamepadEventDto src) {
        return q -> update(src, q, new CycleAvoidingMappingContext());
    }

}
