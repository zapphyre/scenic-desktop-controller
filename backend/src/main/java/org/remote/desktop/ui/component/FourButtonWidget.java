package org.remote.desktop.ui.component;

import javafx.scene.Group;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.remote.desktop.ui.model.ButtonsSettings;

import java.util.HashMap;
import java.util.Map;

public class FourButtonWidget extends Group {

    private final double radius;
    private final double shift;
    private final Map<String, ButtonNode> buttons = new HashMap<>();

    public FourButtonWidget(
            ButtonsSettings y, ButtonsSettings b, ButtonsSettings a, ButtonsSettings x,
            double widgetSize, double textSize) {

        this.radius = widgetSize / 6;
        double centerOffset = widgetSize / 2;
        double circleOffset = centerOffset - radius;
        this.shift = circleOffset + radius;

        // Create buttons with shading
        createButton("Y", y, 0 + shift, -circleOffset + shift, textSize);
        createButton("B", b, -circleOffset + shift, 0 + shift, textSize);
        createButton("A", a, circleOffset + shift, 0 + shift, textSize);
        createButton("X", x, 0 + shift, circleOffset + shift, textSize);
    }

    private void createButton(String key, ButtonsSettings settings, double x, double y, double textSize) {
        // Outer bezel (darker ring)
        Circle bezel = new Circle(x, y, radius + 3); // Slightly larger for depth
        bezel.setFill(settings.getBaseColor().darker().darker());

        // Main button circle with 3D gradient
        Circle circle = new Circle(x, y, radius);
        circle.setFill(create3DGradient(settings.getBaseColor(), false));
        circle.setOpacity(settings.getAlpha());

        // Soft shine reflection (initially top-left)
        Circle shine = new Circle(x - radius * 0.37, y - radius * 0.3, radius * 0.28);
        shine.setFill(new RadialGradient(
                0, 0,
                0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 255, 255, 0.4)),  // Soft white center
                new Stop(1, Color.rgb(255, 255, 255, 0.001))  // Transparent edges
        ));
        shine.setMouseTransparent(true);

        // Stroke (outline) text for better readability
        Text strokeText = new Text(settings.getLetters());
        strokeText.setFont(Font.font(textSize));
        strokeText.setFill(Color.TRANSPARENT);
        strokeText.setStroke(Color.BLACK);
        strokeText.setStrokeWidth(2);
        strokeText.setStrokeType(StrokeType.OUTSIDE);

        // Main fill text
        Text fillText = new Text(settings.getLetters());
        fillText.setFont(Font.font(textSize));
        fillText.setFill(settings.getTextColor());

        // Center both text layers in a group
        Group labelGroup = new Group(strokeText, fillText);
        labelGroup.setLayoutX(x - fillText.getLayoutBounds().getWidth() / 2);
        labelGroup.setLayoutY(y + fillText.getLayoutBounds().getHeight() / 4);

        getChildren().addAll(bezel, circle, shine, labelGroup);

        buttons.put(key.toUpperCase(), new ButtonNode(bezel, circle, shine, labelGroup, strokeText, fillText, settings));
    }

    private Paint create3DGradient(Color baseColor, boolean flipped) {
        return new RadialGradient(
                0, 0,
                0.4, 0.5, // Center of circle
                0.7, // Radius (proportional to circle size)
                true, CycleMethod.NO_CYCLE,
                flipped
                        ? new Stop(0, baseColor.darker())
                        : new Stop(0, baseColor.brighter().brighter()),
                flipped
                        ? new Stop(1, baseColor.brighter().brighter())
                        : new Stop(1, baseColor.darker())
        );
    }

    /**
     * Simulates pressing a button: flips the 3D shading
     */
    public void activate(String buttonKey, boolean flipped) {
        ButtonNode btn = buttons.get(buttonKey.toUpperCase());
        if (btn != null) {
            btn.circle.setFill(create3DGradient(btn.settings.getBaseColor(), flipped));

            // Flip shine to the other side
            double x = btn.circle.getCenterX();
            double y = btn.circle.getCenterY();
            double offsetX = radius * 0.37;
            double offsetY = radius * 0.3;

            btn.shine.setCenterX(flipped ? x + offsetX : x - offsetX);
            btn.shine.setCenterY(flipped ? y + offsetY : y - offsetY);

            btn.shine.setOpacity(flipped ? 0.3 : 0.5);
        }
    }


    /**
     * Resets button shading back to unpressed
     */
    public void deactivate(String buttonKey) {
        ButtonNode btn = buttons.get(buttonKey.toUpperCase());
        if (btn != null) {
            btn.circle.setFill(create3DGradient(btn.settings.getBaseColor(), false));
        }
    }

    public void setButtonLabel(String buttonKey, String label) {
        ButtonNode btn = buttons.get(buttonKey.toUpperCase());
        if (btn != null) {
            btn.strokeText.setText(label);
            btn.text.setText(label);

            // Use text to measure width & height for centering
            double width = btn.text.getLayoutBounds().getWidth();
            double height = btn.text.getLayoutBounds().getHeight();

            double x = btn.circle.getCenterX();
            double y = btn.circle.getCenterY();

            btn.labelGroup.setLayoutX(x - width / 2);
            btn.labelGroup.setLayoutY(y + height / 4);
        }
    }

    private static class ButtonNode {
        Circle bezel;
        Circle circle;
        Circle shine;
        Group labelGroup;
        Text strokeText;
        Text text;
        ButtonsSettings settings;

        ButtonNode(Circle bezel, Circle circle, Circle shine, Group labelGroup, Text strokeText, Text text, ButtonsSettings settings) {
            this.bezel = bezel;
            this.circle = circle;
            this.shine = shine;
            this.labelGroup = labelGroup;
            this.strokeText = strokeText;
            this.text = text;
            this.settings = settings;
        }
    }


}
