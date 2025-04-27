package org.remote.desktop.model;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.ui.model.ButtonInputProcessor;
import org.remote.desktop.ui.model.IndexLetterAction;

@Value
@SuperBuilder
public class ReplacingUiButtonAdapter extends UiButtonBase {

    @Override
    public IndexLetterAction processTouch(ButtonInputProcessor processor) {
        return q -> processor.  asDeletingLong(getElements().get(q).getLabel());
    }
}
