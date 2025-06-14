package org.remote.desktop.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.remote.desktop.db.entity.ButtonEvent;
import org.remote.desktop.model.dto.ButtonEventDto;
import org.remote.desktop.model.vto.ButtonEventVto;

@Mapper(componentModel = "spring", uses = {XdoActionMapper.class})
public interface ButtonEventMapper {

    @Mapping(target = "multiplicity", defaultValue = "CLICK")
    ButtonEventDto map(ButtonEvent entity, @Context CycleAvoidingMappingContext ctx);

    @Named("mapVto")
    ButtonEventVto mapVto(ButtonEvent entity, @Context CycleAvoidingMappingContext ctx);
}
