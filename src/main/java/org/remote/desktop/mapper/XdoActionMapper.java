package org.remote.desktop.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.remote.desktop.entity.XdoAction;
import org.remote.desktop.model.XdoActionVto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface XdoActionMapper {

    XdoAction map(XdoActionVto vto, @Context CycleAvoidingMappingContext ctx);

    XdoActionVto map(XdoAction entity, @Context CycleAvoidingMappingContext ctx);

    void update(XdoActionVto from, @MappingTarget XdoAction to, @Context CycleAvoidingMappingContext ctx);

    default Consumer<XdoAction> updater(XdoActionVto from) {
        return q -> update(from, q, new CycleAvoidingMappingContext());
    }
}
