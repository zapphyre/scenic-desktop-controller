package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.GPadEvent;
import org.remote.desktop.model.GPadEventVto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ActionMapper {

    GPadEventVto map(GPadEvent GPadEvent, @Context CycleAvoidingMappingContext ctx);

    GPadEvent map(GPadEventVto vto, @Context CycleAvoidingMappingContext ctx);

//    @Mapping(target = "", ignore = true)
    void update(GPadEventVto src, @MappingTarget GPadEvent tgt, @Context CycleAvoidingMappingContext ctx);

    default Consumer<GPadEvent> updater(GPadEventVto src) {
        return q -> update(src, q, new CycleAvoidingMappingContext());
    }

}
