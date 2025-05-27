package org.remote.desktop.model;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.ui.model.ButtonInputProcessor;
import org.remote.desktop.ui.model.IndexLetterAction;

@Value
@SuperBuilder
public class CharAddButtonTouch extends UiButtonBase { // to add individual character after long-press

    @Override
    public IndexLetterAction processTouch(ButtonInputProcessor processor) {
        return q -> processor.asLetter(getLettersOnButton().get(q).getLabel());
    }

    public UiButtonBase getLongTouchHandler() {
        return ReplacingUiButtonAdapter.builder()
                .group(group)
                .button(button)
                .lettersOnButton(lettersOnButton)
                .build();
    }
}
