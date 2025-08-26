package org.remote.desktop.ui.trigger;

import java.util.Map;

public interface SelectedGetter<L, R> {

    Map.Entry<L, R> getSelected();
}
