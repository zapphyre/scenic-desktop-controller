package org.remote.desktop.model;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.ui.model.ButtonInputProcessor;
import org.remote.desktop.ui.model.IndexLetterAction;
import org.remote.desktop.util.IdxWordTx;
import org.remote.desktop.util.WordGenFun;

@Value
@SuperBuilder
public class FunctionalButtonTouch extends UiButtonBase {

    IdxWordTx transform;

    @Override
    public IndexLetterAction processTouch(ButtonInputProcessor processor) {
        return q -> processor.asFunction(transform);
    }
}
