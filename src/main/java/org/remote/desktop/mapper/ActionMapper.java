package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.GPadEvent;
import org.remote.desktop.model.dto.GPadEventDto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ActionMapper {

    GPadEventDto map(GPadEvent GPadEvent, @Context CycleAvoidingMappingContext ctx);

    GPadEvent map(GPadEventDto vto, @Context CycleAvoidingMappingContext ctx);

//    @Mapping(target = "", ignore = true)
    void update(GPadEventDto src, @MappingTarget GPadEvent tgt, @Context CycleAvoidingMappingContext ctx);

    default Consumer<GPadEvent> updater(GPadEventDto src) {
        return q -> update(src, q, new CycleAvoidingMappingContext());
    }

}
