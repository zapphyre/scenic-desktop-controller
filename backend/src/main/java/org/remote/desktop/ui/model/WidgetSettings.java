package org.remote.desktop.ui.model;

import javafx.scene.paint.Color;

public record WidgetSettings(double letterSize,        // From constructor
                             Color arcDefaultFillColor,// From constructor
                             double arcDefaultAlpha,   // From constructor
                             Color highlightedColor,   // From constructor
                             Color textColor,          // From constructor
                             double scaleFactor,       // From createScene
                             double innerRadius,       // From createScene
                             double outerRadius,       // From createScene
                             String[] letterGroups,    // From createScene
                             double rotationAngle) {
}
