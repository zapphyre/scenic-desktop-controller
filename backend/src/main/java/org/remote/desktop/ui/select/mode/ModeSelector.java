package org.remote.desktop.ui.select.mode;

import org.remote.desktop.mode.model.EMode;
import org.remote.desktop.ui.select.UnoSelectApplication;
import org.remote.desktop.ui.select.trigger.TriggerUpdateCallback;

import java.util.List;
import java.util.function.Function;

public class ModeSelector {

    UnoSelectApplication<EMode> application = new UnoSelectApplication<>();

    public TriggerUpdateCallback<EMode> setItems(List<? extends EMode> items,
                                                 Function<? super EMode, String> labelGetter) {
        return application.setItems(items, Enum::name);
    }
}
