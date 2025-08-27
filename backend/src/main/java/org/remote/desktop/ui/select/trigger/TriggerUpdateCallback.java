package org.remote.desktop.ui.select.trigger;

import java.util.function.Consumer;

@FunctionalInterface
public interface TriggerUpdateCallback {

    void update(Consumer<TriggerUpdate> update);
}
