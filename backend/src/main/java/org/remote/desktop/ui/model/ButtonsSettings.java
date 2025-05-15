package org.remote.desktop.ui.model;

import javafx.scene.paint.Color;
import lombok.Builder;
import lombok.Value;
import org.remote.desktop.model.UiButtonBase;

@Value
@Builder
public class ButtonsSettings {
    Color baseColor;
    double alpha;
    Color textColor;
    char trieKey;

    int charCount;
//    List<LF> elements;
    UiButtonBase uiButton;
}
