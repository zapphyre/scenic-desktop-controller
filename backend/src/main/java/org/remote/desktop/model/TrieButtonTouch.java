package org.remote.desktop.model;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.ui.model.ButtonInputProcessor;
import org.remote.desktop.ui.model.IndexLetterAction;

@Value
@SuperBuilder
public class TrieButtonTouch extends UiButtonBase {

    char trieCode;

    @Override
    public IndexLetterAction processTouch(ButtonInputProcessor processor) {
        return q -> processor.asTrieChar(trieCode);
    }

    @Override
    public UiButtonBase getLongTouchHandler() {
        return CharAddButtonTouch.builder()
                .group(group)
                .elements(elements)
                .button(button)
                .build();
    }
}
