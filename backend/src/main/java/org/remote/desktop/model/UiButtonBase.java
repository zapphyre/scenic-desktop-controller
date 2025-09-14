package org.remote.desktop.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.ui.model.ButtonInputProcessor;
import org.remote.desktop.ui.model.EKeyboardInputButton;
import org.remote.desktop.ui.model.IndexLetterAction;

import java.util.List;

@Data
@SuperBuilder
public abstract class UiButtonBase {

    int group;
    EKeyboardInputButton button;

    @Builder.Default
    List<LF> lettersOnButton = List.of();

    public abstract IndexLetterAction processTouch(ButtonInputProcessor processor);

    public UiButtonBase getLongTouchHandler() {
        return this;
    }
}
