package org.remote.desktop.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.remote.desktop.entity.XdoAction;
import org.remote.desktop.model.XdoActionVto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface XdoActionMapper {

    XdoAction map(XdoActionVto vto, @Context CycleAvoidingMappingContext ctx);

    XdoActionVto map(XdoAction entity, @Context CycleAvoidingMappingContext ctx);

    void update(XdoAction from, @MappingTarget XdoActionVto to, @Context CycleAvoidingMappingContext ctx);
}
