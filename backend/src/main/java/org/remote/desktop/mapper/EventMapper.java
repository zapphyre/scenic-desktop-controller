package org.remote.desktop.mapper;

import org.mapstruct.*;
import org.remote.desktop.db.entity.Action;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.db.repository.ModeRepository;
import org.remote.desktop.model.dto.EventDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.model.vto.XdoActionVto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {GestureEventMapper.class, ButtonEventMapper.class, GestureMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface EventMapper {

    @Mapping(target = "buttonEvent", source = "buttonEvent", qualifiedByName = "map")
    @Mapping(target = "actions", source = "actions", qualifiedByName = "maptoDto")
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
//    @Mapping(target = "actions", source = "actions", qualifiedByName = "map")
    EventVto map(Event evt);


    @Named("mapActionsWithMode")
    default List<Action> mapActionsWithMode(List<Action> actions, @Context ModeRepository modeRepository) {
        return Optional.ofNullable(actions)
                .orElseGet(Collections::emptyList).stream()
                .map(action -> action.withMode(modeRepository.findByAdapterMode(action.getMode().getAdapterMode())))
                .collect(Collectors.toList());
    }

    @Mapping(target = "actions", source = "entity.actions", qualifiedByName = "mapActionsWithMode")
    Event rebindMode(Event entity, @Context ModeRepository modeRepository);

    default Function<Event, Event> rebindMode(ModeRepository modeRepository) {
        return q -> rebindMode(q, modeRepository);
    }

    @Mapping(target = "id", source = "vto.id")
    @Mapping(target = "event", source = "event")
    Action mapXdoEvent(XdoActionVto vto, Event event);

    default Function<XdoActionVto, Action> mapXdoEvent(Event event) {
        return q -> mapXdoEvent(q, event);
    }

    List<EventVto> map(Iterable<Event> events);

    @Mapping(target = "id", source = "src.id")

    /*
    * I ignore actions here, b/c i want them to be PERSIST cascading on event, b/c I want event to be able
    * to save when winder scene initializes; it throws detached entity exception when I update EventVto and it comes with
    * actions on it and PERSIST cascading on event is enabled
    * */
    @Mapping(target = "actions", ignore = true)
    void update(@MappingTarget Event tgt, EventVto src, Scene scene, Scene nextScene);

    default Consumer<Event> update(EventVto src, Scene parent, Scene next) {
        return q -> update(q, src, parent, next);
    }

    @Mapping(target = "id", source = "vto.id")
    Event map(EventVto vto, Scene scene, Scene nextScene, @Context CycleAvoidingMappingContext ctx);

    default Function<EventVto, Event> map(Scene parent, Scene next) {
        return q -> map(q, parent, next, new CycleAvoidingMappingContext());
    }

    Action map(XdoActionDto dto, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "mode", source = "mode.adapterMode")
    XdoActionDto map(Action entity, @Context CycleAvoidingMappingContext ctx);

    @Named("maptoDto")
    List<XdoActionDto> maptoDto(Iterable<Action> entities, @Context CycleAvoidingMappingContext ctx);

    List<Action> mapDtos(Iterable<XdoActionDto> entities, @Context CycleAvoidingMappingContext ctx);

    @Mapping(target = "eventFk", source = "event.id")
    @Mapping(target = "mode", source = "mode.adapterMode")
    XdoActionVto mapToVto(Action entity);

    @Named("map")
    List<XdoActionVto> map(List<Action> entities);

    void update(XdoActionDto from, @MappingTarget Action to, @Context CycleAvoidingMappingContext ctx);

    default Consumer<Action> updater(XdoActionDto from) {
        return q -> update(from, q, new CycleAvoidingMappingContext());
    }

    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "event", source = "gEvt")
    @Mapping(target = "mode", ignore = true)
    void update(@MappingTarget Action target, XdoActionVto source, Event gEvt, @Context CycleAvoidingMappingContext ctx);

    default Consumer<Action> update(XdoActionVto source, Event gEvt) {
        return q -> update(q, source, gEvt, new CycleAvoidingMappingContext());
    }
}
