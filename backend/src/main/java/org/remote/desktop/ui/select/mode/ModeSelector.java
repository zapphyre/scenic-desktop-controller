package org.remote.desktop.ui.select.mode;

import lombok.Getter;
import org.remote.desktop.ui.select.UnoSelectApplication;
import org.remote.desktop.ui.select.trigger.TriggerUpdateCallback;

import java.util.List;
import java.util.function.Function;

public class ModeSelector {

    @Getter
    UnoSelectApplication<String> application = new UnoSelectApplication<>();

    public TriggerUpdateCallback<String> setItems(List<? extends String> items,
                                                 Function<? super String, String> labelGetter) {
        return application.setItems(items, labelGetter);
    }
}
