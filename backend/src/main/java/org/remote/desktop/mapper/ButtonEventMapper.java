package org.remote.desktop.mapper;

import org.asmus.model.EButtonAxisMapping;
import org.mapstruct.*;
import org.remote.desktop.db.entity.ButtonEvent;
import org.remote.desktop.model.dto.ButtonEventDto;
import org.remote.desktop.model.vto.ButtonEventVto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {}, builder = @Builder(disableBuilder = true))
public interface ButtonEventMapper {

    // @Data on DTOs, disabled builder are set for mapstruct to avoid stack overflow ButtonEvent -> Event which occours
    // even with CycleAvoidingMappingContext. the reason is that object is put to the context after mappings are done, b/c
    // mapstruct tries to use @RequiredArgsConstructor or when using builder it fails on cast exception b/c builder is put to the ctx.
    // solution is to use non-arg constructor, in that case mapstruct instantiates object and puts it to mapping Ctx right away.

    @Named("map")
    @Mapping(target = "multiplicity", defaultValue = "CLICK")
    ButtonEventDto map(ButtonEvent entity, @Context CycleAvoidingMappingContext ctx);

    @Named("mapVto")
    ButtonEventVto mapVto(ButtonEvent entity, @Context CycleAvoidingMappingContext ctx);

    default EButtonAxisMapping map(String val) {
        return EButtonAxisMapping.valueOf(val);
    }
}