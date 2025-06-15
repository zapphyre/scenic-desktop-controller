package org.remote.desktop.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.vto.XdoActionVto;

import java.util.function.Consumer;
import java.util.function.Function;

@Mapper(componentModel = "spring", uses = GestureMapper.class)
public interface XdoActionMapper {

    XdoAction map(XdoActionDto vto, @Context CycleAvoidingMappingContext ctx);

    XdoActionDto map(XdoAction entity, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "eventFk", source = "event.id")
    XdoActionVto map(XdoAction entity);

    void update(XdoActionDto from, @MappingTarget XdoAction to, @Context CycleAvoidingMappingContext ctx);

    default Consumer<XdoAction> updater(XdoActionDto from) {
        return q -> update(from, q, new CycleAvoidingMappingContext());
    }

    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "event", source = "gEvt")
    void update(@MappingTarget XdoAction target, XdoActionVto source, Event gEvt);

    default Consumer<XdoAction> update(XdoActionVto source, Event gEvt) {
        return q -> update(q, source, gEvt);
    }

    @Mapping(target = "id", source = "vto.id")
    XdoAction map(XdoActionVto vto, Event gamepadEvent);

    default Function<XdoActionVto, XdoAction> map(Event gamepadEvent) {
        return q -> map(q, gamepadEvent);
    }
}
