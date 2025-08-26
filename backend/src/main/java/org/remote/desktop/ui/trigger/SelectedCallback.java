package org.remote.desktop.ui.trigger;

import java.util.Map;
import java.util.function.Consumer;

public interface SelectedCallback<L, R> {

    void selected(Consumer<Map.Entry<L, R>> callback);
}
