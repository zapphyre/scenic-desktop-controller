package org.remote.desktop.model;

import java.util.List;
import java.util.Objects;

public interface GamepadEventContainer<T, S extends GamepadEventContainer<T, S>> {

    List<T> getGamepadEvents();

    S getInherits();

}
