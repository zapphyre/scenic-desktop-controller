package org.remote.desktop.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.remote.desktop.db.entity.GestureEvent;
import org.remote.desktop.model.dto.GestureEventDto;
import org.remote.desktop.model.vto.GestureEventVto;

@Mapper(componentModel = "spring")
public interface GestureEventMapper {

//    GestureEventDto map(GestureEvent entity, @Context CycleAvoidingMappingContext ctx);

    @Named("mapGestureEvent")
    @Mapping(target = "rightStickGestureFk", source = "rightStickGesture.id")
    @Mapping(target = "leftStickGestureFk", source = "leftStickGesture.id")
    GestureEventVto map(GestureEvent entity);
}
