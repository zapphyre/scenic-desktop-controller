package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.model.dto.EventDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.model.vto.XdoActionVto;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Mapper(componentModel = "spring",
        uses = {GestureEventMapper.class, ButtonEventMapper.class, GestureMapper.class, GestureMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface EventMapper {

    @Mapping(target = "buttonEvent", source = "buttonEvent", qualifiedByName = "map")
    EventDto map(Event event, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "scene", ignore = true)
    Event map(EventDto vto, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "scene", ignore = true)
    void update(EventDto src, @MappingTarget Event tgt, @Context CycleAvoidingMappingContext ctx);

    default Consumer<Event> updater(EventDto src) {
        return q -> update(src, q, new CycleAvoidingMappingContext());
    }

    // mapstruct can not pick up mapping for this field by itself, it has to be referenced by name `mapGestureEvent`
    @Mapping(target = "gestureEvent", source = "gestureEvent", qualifiedByName = "mapGestureEvent")
    @Mapping(target = "nextSceneFk", source = "nextScene.id")
    @Mapping(target = "parentFk", source = "scene.id")
    EventVto map(Event evt);

    @Mapping(target = "id", source = "vto.id")
    @Mapping(target = "event", source = "event")
    XdoAction mapXdoEvent(XdoActionVto vto, Event event);

    default Function<XdoActionVto, XdoAction> mapXdoEvent(Event event) {
        return q -> mapXdoEvent(q, event);
    }

    List<EventVto> map(Iterable<Event> events);

    @Mapping(target = "id", source = "src.id")
    void update(@MappingTarget Event tgt, EventVto src, Scene scene, Scene nextScene);

    default Consumer<Event> update(EventVto src, Scene parent, Scene next) {
        return q -> update(q, src, parent, next);
    }

    @Mapping(target = "id", source = "vto.id")
    Event map(EventVto vto, Scene scene, Scene nextScene, @Context CycleAvoidingMappingContext ctx);

    default Function<EventVto, Event> map(Scene parent, Scene next) {
        return q -> map(q, parent, next, new CycleAvoidingMappingContext());
    }

    XdoAction map(XdoActionDto dto, @Context CycleAvoidingMappingContext ctx);

    XdoActionDto map(XdoAction entity, @Context CycleAvoidingMappingContext ctx);

    List<XdoActionDto> map(Iterable<XdoAction> entities, @Context CycleAvoidingMappingContext ctx);

    List<XdoAction> mapDtos(Iterable<XdoActionDto> entities, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "eventFk", source = "event.id")
    XdoActionVto mapToVto(XdoAction entity, @Context CycleAvoidingMappingContext ctx);

    void update(XdoActionDto from, @MappingTarget XdoAction to, @Context CycleAvoidingMappingContext ctx);

    default Consumer<XdoAction> updater(XdoActionDto from) {
        return q -> update(from, q, new CycleAvoidingMappingContext());
    }

    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "event", source = "gEvt")
    void update(@MappingTarget XdoAction target, XdoActionVto source, Event gEvt, @Context CycleAvoidingMappingContext ctx);

    default Consumer<XdoAction> update(XdoActionVto source, Event gEvt) {
        return q -> update(q, source, gEvt, new CycleAvoidingMappingContext());
    }
}
