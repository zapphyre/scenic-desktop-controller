package org.remote.desktop.ui.component;

import javafx.scene.Group;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
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
        Circle bezel = new Circle(x, y, radius + 3); // Slightly bigger
        bezel.setFill(settings.getBaseColor().darker().darker());

        // Main button
        Circle circle = new Circle(x, y, radius);
        circle.setFill(create3DGradient(settings.getBaseColor(), false));
        circle.setOpacity(settings.getAlpha());

// Light reflection (shine) — soft & subtle
        Circle shine = new Circle(x - radius * 0.37, y - radius * 0.3, radius * 0.28);
        shine.setFill(new RadialGradient(
                0, 0,
                0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 255, 255, 0.4)),  // Soft white center
                new Stop(1, Color.rgb(255, 255, 255, 0.001))   // Transparent edge
        ));
        shine.setMouseTransparent(true); // Don't block mouse


        // Text label
        Text text = new Text(settings.getLetters());
        text.setFont(Font.font(textSize));
        text.setFill(settings.getTextColor());

        // Center the text
        text.setX(x - text.getLayoutBounds().getWidth() / 2);
        text.setY(y + text.getLayoutBounds().getHeight() / 4);

        getChildren().addAll(bezel, circle, shine, text);

        buttons.put(key.toUpperCase(), new ButtonNode(bezel, circle, shine, text, settings));
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

    /**
     * Sets the label (text) on a specific button
     */
    public void setButtonLabel(String buttonKey, String label) {
        ButtonNode btn = buttons.get(buttonKey.toUpperCase());
        if (btn != null) {
            btn.text.setText(label);
            // Re-center
            double x = btn.circle.getCenterX();
            double y = btn.circle.getCenterY();
            btn.text.setX(x - btn.text.getLayoutBounds().getWidth() / 2);
            btn.text.setY(y + btn.text.getLayoutBounds().getHeight() / 4);
        }
    }

    private static class ButtonNode {
        Circle bezel;
        Circle circle;
        Circle shine;
        Text text;
        ButtonsSettings settings;

        ButtonNode(Circle bezel, Circle circle, Circle shine, Text text, ButtonsSettings settings) {
            this.bezel = bezel;
            this.circle = circle;
            this.shine = shine;
            this.text = text;
            this.settings = settings;
        }
    }

}
