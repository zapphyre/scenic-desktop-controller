package org.remote.desktop.ui.model;

import javafx.scene.paint.Color;
import lombok.Builder;
import lombok.Value;
import org.remote.desktop.util.IdxWordTx;
import org.remote.desktop.util.LetterIdxGetter;
import org.remote.desktop.util.WordGenFun;

@Value
@Builder
public class ButtonsSettings {
    Color baseColor;
    double alpha;
    Color textColor;
    char trieKey;

    int charCount;
    LetterIdxGetter letterIdxGetter;
    IdxWordTx idxTxFun;
}
