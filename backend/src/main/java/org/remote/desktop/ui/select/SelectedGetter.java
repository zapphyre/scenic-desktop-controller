package org.remote.desktop.ui.select;

import java.util.Map;

public interface SelectedGetter<L, R> {

    Map.Entry<L, R> getSelected();
}
