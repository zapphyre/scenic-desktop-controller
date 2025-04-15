package org.remote.desktop.ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.remote.desktop.ui.component.FourButtonWidget;
import org.remote.desktop.ui.model.ButtonsSettings;

public class CircleButtonsInputWidget extends VariableGroupingInputWidgetBase {

    public CircleButtonsInputWidget(double widgetSize, double letterSize, Color arcDefaultFillColor, double arcDefaultAlpha, Color highlightedColor, Color textColor, int letterGroupCount, String title) {
        super(widgetSize, letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, letterGroupCount, title);
    }

    @Override
    Pane getRightWidget() {
        ButtonsSettings bs = ButtonsSettings.builder()
                .baseColor(Color.BURLYWOOD)
                .alpha(.8)
                .textColor(Color.DARKGOLDENROD)
                .letters("A B C D")
                .build();

        return new FourButtonWidget(bs, bs, bs, bs, (widgetSize * 2) * scaleFactor, 24);
    }

    @Override
    public int setGroupActive(int index) {
        return 0;
    }

    @Override
    public char setElementActive(int index) {
        return 0;
    }
}
