package org.remote.desktop.ui.component;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.remote.desktop.ui.model.ButtonsSettings;
import org.remote.desktop.ui.model.EActionButton;

import java.util.HashMap;
import java.util.Map;

import static org.remote.desktop.ui.model.EActionButton.*;

public class FourButtonWidget extends Pane {

    private final double radius;
    private final double shift;
    private final Map<EActionButton, ButtonNode> buttons = new HashMap<>();

    public FourButtonWidget(Map<EActionButton, ButtonsSettings> defs, double widgetSize, double textSize) {
        this.radius = widgetSize / 6;
        double centerOffset = widgetSize / 2;
        double circleOffset = centerOffset - radius;
        this.shift = circleOffset + radius;

        // Create buttons with shading
        createButton(Y, defs.get(Y), 0 + shift, -circleOffset + shift, textSize);
        createButton(X, defs.get(X), -circleOffset + shift, 0 + shift, textSize);
        createButton(B, defs.get(B), circleOffset + shift, 0 + shift, textSize);
        createButton(A, defs.get(A), 0 + shift, circleOffset + shift, textSize);

        setTranslateY(5);
    }

    private void createButton(EActionButton key, ButtonsSettings settings, double x, double y, double textSize) {
        // Outer bezel (darker ring)
        int bezelWidth = 3;
        Circle bezel = new Circle(x, y, radius + bezelWidth); // Slightly larger for depth
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

        TextContainer textContainer = new TextContainer();
        for (char c : settings.getLetters().toCharArray()) {
            textContainer.addText(String.valueOf(c));
        }

        HBox labelGroup = new HBox();
        labelGroup.setAlignment(Pos.BOTTOM_CENTER);
        for (char c : settings.getLetters().toCharArray()) {
            // Stroke (outline) text for better readability
            Text strokeText = new Text(String.valueOf(c));
            if (String.valueOf(c).equals("A"))
                strokeText.setFont(Font.font(42));
            else
                strokeText.setFont(Font.font(textSize));
            strokeText.setFill(Color.TRANSPARENT);
            strokeText.setStroke(Color.BLACK);
            strokeText.setStrokeWidth(2);
            strokeText.setStrokeType(StrokeType.OUTSIDE);

            // Main fill text
            Text fillText = new Text(String.valueOf(c));
            if (String.valueOf(c).equals("A"))
                fillText.setFont(Font.font(42));
            else
                fillText.setFont(Font.font(textSize));
            fillText.setFill(settings.getTextColor());

            Group group = new Group(strokeText, fillText);
            labelGroup.getChildren().add(group);
        }
        labelGroup.autosize();
        labelGroup.setLayoutX(x - labelGroup.getLayoutBounds().getWidth() / 2);
        labelGroup.setLayoutY(y - labelGroup.getLayoutBounds().getHeight() / 2);

        getChildren().addAll(bezel, circle, shine, labelGroup);

        buttons.put(key, new ButtonNode(bezel, circle, shine, null, settings));
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
    public char activate(EActionButton buttonKey) {
        ButtonNode btn = buttons.get(buttonKey);
        boolean act = btn.active = !btn.active;
        btn.circle.setFill(create3DGradient(btn.settings.getBaseColor(), act));

        // Flip shine to the other side
        double x = btn.circle.getCenterX();
        double y = btn.circle.getCenterY();
        double offsetX = radius * 0.37;
        double offsetY = radius * 0.3;

        btn.shine.setCenterX(act ? x + offsetX : x - offsetX);
        btn.shine.setCenterY(act ? y + offsetY : y - offsetY);

        btn.shine.setOpacity(act ? 0.3 : 0.5);

        return btn.settings.getTrieKey();
    }

    /**
     * Resets button shading back to unpressed
     */
    public void deactivate(EActionButton buttonKey) {
        ButtonNode btn = buttons.get(buttonKey);
        if (btn != null) {
            btn.circle.setFill(create3DGradient(btn.settings.getBaseColor(), false));
        }
    }

    public void setButtonLabel(int button, String label) {
        ButtonNode btn = buttons.get(button);
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
        boolean active;
        Circle bezel;
        Circle circle;
        Circle shine;
        Group labelGroup;
        Text strokeText;
        Text text;
        ButtonsSettings settings;

        ButtonNode(Circle bezel, Circle circle, Circle shine, Group labelGroup, ButtonsSettings settings) {
            this.bezel = bezel;
            this.circle = circle;
            this.shine = shine;
            this.labelGroup = labelGroup;
//            this.strokeText = strokeText;
//            this.text = text;
            this.settings = settings;
        }
    }


}
