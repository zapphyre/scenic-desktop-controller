package org.remote.desktop.ui.component;

import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.remote.desktop.ui.model.ButtonsSettings;
public class FourButtonWidget extends Group {

    public FourButtonWidget(
            ButtonsSettings y, ButtonsSettings b, ButtonsSettings a, ButtonsSettings x,
            double widgetSize, double textSize) {
        // Calculate circle radius and positions
        double radius = widgetSize / 6; // Ensures circles fit well
        double centerOffset = widgetSize / 2;
        double circleOffset = centerOffset - radius;

        // Create circles with their settings
        createButton(y, 0, -circleOffset, radius, textSize); // Y (top)
        createButton(b, -circleOffset, 0, radius, textSize); // B (left)
        createButton(a, circleOffset, 0, radius, textSize);  // A (right)
        createButton(x, 0, circleOffset, radius, textSize);  // X (bottom)
    }

    private void createButton(ButtonsSettings settings, double x, double y, double radius, double textSize) {
        // Create circle
        Circle circle = new Circle(x, y, radius);
        circle.setFill(settings.getBaseColor());
        circle.setOpacity(settings.getAlpha());

        // Create Text
        Text text = new Text(settings.getLetters());
        text.setFont(Font.font(textSize));
        text.setFill(settings.getTextColor());

        // Wrap in Group instead of TextFlow (simpler centering)
        Group textGroup = new Group(text);

        // Position the text centered over the circle
        textGroup.setLayoutX(x - text.getLayoutBounds().getWidth() / 2);
        textGroup.setLayoutY(y + text.getLayoutBounds().getHeight() / 4); // adjust for baseline

        // Add to group
        getChildren().addAll(circle, textGroup);
    }
}