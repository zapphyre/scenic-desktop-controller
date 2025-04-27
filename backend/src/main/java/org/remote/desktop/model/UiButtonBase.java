package org.remote.desktop.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.ui.model.ButtonInputProcessor;
import org.remote.desktop.ui.model.EActionButton;
import org.remote.desktop.ui.model.IndexLetterAction;
import org.remote.desktop.ui.model.ModifiedIndexedTransformer;

import java.util.List;

@Data
@SuperBuilder
public abstract class UiButtonBase {

    int group;
    EActionButton button;

    @Builder.Default
    List<LF> elements = List.of();

    public abstract IndexLetterAction processTouch(ButtonInputProcessor processor);

    public ModifiedIndexedTransformer processTouchModified(ButtonInputProcessor processor) {
        return q -> p -> processTouch(processor).actOnIndexLetter(p);
    }

    public UiButtonBase getLongTouchHandler() {
        return this;
    }
}
