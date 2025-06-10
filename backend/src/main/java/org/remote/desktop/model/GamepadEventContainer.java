package org.remote.desktop.model;

import java.util.*;

public interface GamepadEventContainer<T, S extends GamepadEventContainer<T, S>> {

    List<T> getEvents();

    Set<S> getInheritsFrom();

    default List<S> getInheritsFromSafe() {
        return Optional.ofNullable(getInheritsFrom())
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);
    }

}
