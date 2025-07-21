package org.remote.desktop.util;

import org.remote.desktop.model.GamepadEventContainer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;


public class RecursiveScraper<T, S extends GamepadEventContainer<T, S>> {

    // might be useful when I want to have some base/system scene that is on background of any other scene
    public Function<S, Set<T>> scrapeActionsRecursiveWithCurrentOn(GamepadEventContainer<T, S> baseScene) {
        // in this order b/c lambda param is current recognized; scraped are off of it and base are additions
        return q -> Stream.of(q.getEvents(), scrapeActionsRecursive(q), baseScene.getEvents())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Set<T> scrapeActionsRecursiveWithCurrent(GamepadEventContainer<T, S> sceneDto) {
        // in this order b/c events from current sceneDto will take precedence eg. equal ones from second set (scraped)
        // won't get merged in to result set
        return Stream.of(sceneDto.getEvents(), scrapeActionsRecursive(sceneDto))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Set<T> scrapeActionsRecursive(GamepadEventContainer<T, S> sceneDto) {
        return sceneDto == null ? Set.of() : scrapeActionsRecursive(sceneDto, new LinkedHashSet<>());
    }

    public Set<T> scrapeActionsRecursive(GamepadEventContainer<T, S> sceneDto, Set<T> gamepadEventDtos) {
        if (!sceneDto.getInheritsFromSafe().isEmpty())
            sceneDto.getInheritsFrom().forEach(q -> scrapeActionsRecursive(q, gamepadEventDtos));

        sceneDto.getInheritsFromSafe().stream()
                .map(GamepadEventContainer::getEvents)
                .forEach(gamepadEventDtos::addAll);

        return gamepadEventDtos;
    }
}

