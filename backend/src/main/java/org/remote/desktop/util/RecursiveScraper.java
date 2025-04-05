package org.remote.desktop.util;

import org.remote.desktop.model.GamepadEventContainer;

import java.util.*;


public class RecursiveScraper<T, S extends GamepadEventContainer<T, S>> {
    public Set<T> scrapeActionsRecursive(GamepadEventContainer<T, S> sceneDto) {
        return sceneDto == null ? Set.of() : scrapeActionsRecursive(sceneDto, new HashSet<>());
    }

    public Set<T> scrapeActionsRecursive(GamepadEventContainer<T, S> sceneDto, Set<T> gamepadEventDtos) {
        if (!sceneDto.getInheritsFromSafe().isEmpty())
            sceneDto.getInheritsFrom().forEach(q -> scrapeActionsRecursive(q, gamepadEventDtos));

        Optional.of(sceneDto)
                .map(GamepadEventContainer::getGamepadEvents)
                .ifPresent(gamepadEventDtos::addAll);

        return gamepadEventDtos;
    }
}

