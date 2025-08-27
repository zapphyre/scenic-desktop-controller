package org.remote.desktop.ui.select.axis;

import java.util.function.Consumer;

@FunctionalInterface
public interface SelectedCallback<L, R> {

    void selected(Consumer<AxisUpdate<L,R>> callback);
}
