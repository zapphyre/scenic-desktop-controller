package org.remote.desktop.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.remote.desktop.entity.Action;
import org.remote.desktop.model.ActionVto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ActionMapper {

    ActionVto map(Action action, @Context CycleAvoidingMappingContext ctx);

    Action map(ActionVto vto, @Context CycleAvoidingMappingContext ctx);

    void update(Action src, @MappingTarget ActionVto tgt, @Context CycleAvoidingMappingContext ctx);
}
