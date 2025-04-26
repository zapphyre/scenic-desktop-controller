package org.remote.desktop.model;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.ui.model.ButtonInputProcessor;
import org.remote.desktop.ui.model.IndexLetterAction;

@Value
@SuperBuilder
public class CharAddButtonTouch extends UiButtonBase {

    @Override
    public IndexLetterAction processTouch(ButtonInputProcessor processor) {
        return q -> processor.asLetter(getElements().get(q).getLabel());
    }

    @Override
    public UiButtonBase getLongTouchHandler() {
        return ReplacingUiButtonAdapter.builder()
                .group(group)
                .button(button)
                .elements(elements)
                .build();
    }
}
