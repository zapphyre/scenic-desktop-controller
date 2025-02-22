package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.entity.XdoAction;
import org.remote.desktop.model.XdoActionVto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface XdoActionMapper {

    XdoAction map(XdoActionVto vto, @Context CycleAvoidingMappingContext ctx);

    XdoActionVto map(XdoAction entity, @Context CycleAvoidingMappingContext ctx);

    void update(XdoActionVto from, @MappingTarget XdoAction to, @Context CycleAvoidingMappingContext ctx);

//    @AfterMapping
    default void relinkToActions(@MappingTarget XdoAction to, XdoActionVto from) {
        from.getGPadEvent().setActions(to.getGPadEvent().getActions().stream().map(q -> this.map(q, new CycleAvoidingMappingContext())).toList());
    }

    default Consumer<XdoAction> updater(XdoActionVto from) {
        return q -> update(from, q, new CycleAvoidingMappingContext());
    }
}
