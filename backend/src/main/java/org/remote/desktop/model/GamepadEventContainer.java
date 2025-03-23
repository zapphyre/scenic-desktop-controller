package org.remote.desktop.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface GamepadEventContainer<T, S extends GamepadEventContainer<T, S>> {

    List<T> getGamepadEvents();

    List<S> getInheritsFrom();

    default List<S> getInheritsFromSafe() {
        return Optional.ofNullable(getInheritsFrom())
                .orElseGet(List::of);
    }

}
