package org.remote.desktop.ui.select.trigger;

import java.util.function.Consumer;

@FunctionalInterface
public interface TriggerUpdateCallback<T> {

    void update(Consumer<UiSelectUpdate<T>> update);
}
