package org.remote.desktop.mapper;

import org.asmus.model.EButtonAxisMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.remote.desktop.model.dto.EventDto;
import org.remote.desktop.model.dto.XdoActionDto;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface ActivatorGroupingEventMapper {

    @Mapping(target = "buttonEvent.event", ignore = true)
    @Mapping(target = "actions", ignore = true)
    EventDto copyEventDto(EventDto source);

    default List<XdoActionDto> extractNullActions(EventDto dto) {
        return dto.getActions().stream()
                .filter(nullActivator)
                .toList();
    }

    Predicate<XdoActionDto> nullActivator = xdoActionDto -> xdoActionDto.getActivator() == null;

    default Map<String, Integer> collectActivatorIndices(EventDto dto) {
        return IntStream.range(0, dto.getActions().size())
                .boxed()
                .filter(i -> dto.getActions().get(i).getActivator() != null)
                .collect(Collectors.toMap(
                        i -> dto.getActions().get(i).getActivator(),
                        Function.identity(),
                        Math::min,
                        LinkedHashMap::new
                ));
    }

    default Function<Map.Entry<String, Integer>, EventDto> createEventFromEntry(EventDto source, List<XdoActionDto> nullActions) {
        return entry -> {
            int cutoff = entry.getValue() + 1;
            List<XdoActionDto> preActivatorActions = new ArrayList<>(source.getActions().subList(0, cutoff));
            List<XdoActionDto> remainingNulls = nullActions.subList(Math.min(nullActions.size(), cutoff) - 1, nullActions.size());

            List<XdoActionDto> merged = new ArrayList<>(preActivatorActions);
            merged.addAll(remainingNulls);

            EventDto event = copyEventDto(source);
            event.setActions(merged);

            Optional.ofNullable(event.getButtonEvent())
                    .ifPresent(be -> be.setModifiers(mergeModifiers(be.getModifiers(), entry.getKey())));

            return event;
        };
    }

    default Set<EButtonAxisMapping> mergeModifiers(Set<EButtonAxisMapping> modifiers, String activator) {
        return Optional.ofNullable(modifiers)
                .map(HashSet::new).orElseGet(HashSet::new).stream()
                .collect(Collectors.collectingAndThen(Collectors.toSet(), addActivator(activator)));
    }

    default Function<Set<EButtonAxisMapping>, Set<EButtonAxisMapping>> addActivator(String activator) {
        return set -> {
            try {
                set.add(EButtonAxisMapping.valueOf(activator));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid EButtonAxisMapping value: " + activator);
            }
            return set;
        };
    }

    default List<EventDto> groupByActivator(EventDto source) {
        List<XdoActionDto> nullActions = extractNullActions(source);

        List<EventDto> nonNullEvents = collectActivatorIndices(source).entrySet().stream()
                .map(createEventFromEntry(source, nullActions))
                .toList();

        EventDto nullEvent = copyEventDto(source);
        nullEvent.setActions(new ArrayList<>(nullActions));

        return Stream.concat(
                        nonNullEvents.stream(),
                        nullEvent.getActions().isEmpty() ?
                                Stream.empty() : Stream.of(nullEvent)
                )
                .toList();
    }
}