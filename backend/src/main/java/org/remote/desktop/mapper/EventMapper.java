package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.ButtonEvent;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.GestureEvent;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.model.dto.ButtonEventDto;
import org.remote.desktop.model.dto.EventDto;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.model.vto.GestureEventVto;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Mapper(componentModel = "spring", uses = {GestureEventMapper.class, ButtonEventMapper.class, XdoActionMapper.class})
public interface EventMapper {

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

    List<EventVto> map(Iterable<Event> events);

    @Mapping(target = "id", source = "src.id")
    void update(@MappingTarget Event tgt, EventVto src, Scene scene, Scene nextScene);

    default Consumer<Event> update(EventVto src, Scene parent, Scene next) {
        return q -> update(q, src, parent, next);
    }

    @Mapping(target = "id", source = "vto.id")
    Event map(EventVto vto, Scene scene, Scene nextScene);

    default Function<EventVto, Event> map(Scene parent, Scene next) {
        return q -> map(q, parent, next);
    }
}
