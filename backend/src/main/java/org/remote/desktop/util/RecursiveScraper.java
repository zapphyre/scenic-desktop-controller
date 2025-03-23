package org.remote.desktop.util;

import org.remote.desktop.model.GamepadEventContainer;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class RecursiveScraper<T, S extends GamepadEventContainer<T, S>> {
    public List<T> scrapeActionsRecursive(GamepadEventContainer<T, S> sceneDto) {
        return sceneDto == null ? List.of() : scrapeActionsRecursive(sceneDto, new LinkedList<>());
    }

    public List<T> scrapeActionsRecursive(GamepadEventContainer<T, S> sceneDto, List<T> gamepadEventDtos) {
        if (!sceneDto.getInheritsFromSafe().isEmpty())
            sceneDto.getInheritsFrom().forEach(q -> scrapeActionsRecursive(q, gamepadEventDtos));

        Optional.of(sceneDto)
                .map(GamepadEventContainer::getGamepadEvents)
                .ifPresent(gamepadEventDtos::addAll);

        return gamepadEventDtos;
    }
}

