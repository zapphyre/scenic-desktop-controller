package org.remote.desktop.ui.model;

import javafx.scene.paint.Color;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ButtonsSettings {
    Color baseColor;
    double alpha;
    Color textColor;
    char trieKey;
    String letters;
}
