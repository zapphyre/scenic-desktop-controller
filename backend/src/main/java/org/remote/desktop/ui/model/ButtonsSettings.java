package org.remote.desktop.ui.model;

import javafx.scene.paint.Color;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ButtonsSettings {
    Color baseColor;
    double alpha;
    Color textColor;

    String letters;
}
