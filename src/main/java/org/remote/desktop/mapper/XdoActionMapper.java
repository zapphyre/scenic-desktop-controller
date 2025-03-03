package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.model.dto.XdoActionDto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring")
public interface XdoActionMapper {

    @Mapping(target = "gamepadEvent", ignore = true)
    XdoAction map(XdoActionDto vto, @Context CycleAvoidingMappingContext ctx);

    XdoActionDto map(XdoAction entity, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "gamepadEvent", ignore = true)
    void update(XdoActionDto from, @MappingTarget XdoAction to, @Context CycleAvoidingMappingContext ctx);

    default Consumer<XdoAction> updater(XdoActionDto from) {
        return q -> update(from, q, new CycleAvoidingMappingContext());
    }
}
