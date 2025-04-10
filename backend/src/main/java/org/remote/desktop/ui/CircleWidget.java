package org.remote.desktop.ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import org.remote.desktop.ui.model.WidgetSettings;

import java.util.LinkedList;
import java.util.List;

public class CircleWidget extends Scene {
    private final List<Path> slices = new LinkedList<>();
    private final List<Group> labelGroups = new LinkedList<>();
    private final double scaleFactor;
    private final double innerRadius;
    private final double outerRadius;
    private final double rotationAngle;
    private final double centerX;
    private final double centerY;
    private final Pane root;
    private final Group slicesGroup;
    private final double letterSize;
    private final Color arcDefaultFillColor;
    private final double arcDefaultAlpha;
    private final Color highlightedColor;
    private final Color textColor;

    public CircleWidget(WidgetSettings settings) {
        super(new Pane(), 2 * (2 * settings.outerRadius() * settings.scaleFactor() + 50 * settings.scaleFactor()), 200 * settings.scaleFactor());
        this.root = (Pane) getRoot();
        this.root.setStyle("-fx-background-color: null;"); // Transparent background

        // Assign settings to fields
        this.letterSize = settings.letterSize();
        this.arcDefaultFillColor = settings.arcDefaultFillColor();
        this.arcDefaultAlpha = settings.arcDefaultAlpha();
        this.highlightedColor = settings.highlightedColor();
        this.textColor = settings.textColor();
        this.scaleFactor = settings.scaleFactor();
        this.innerRadius = settings.innerRadius();
        this.outerRadius = settings.outerRadius();
        this.rotationAngle = settings.rotationAngle();
        this.centerX = 150 * scaleFactor;
        this.centerY = 100 * scaleFactor;

        // Initialize UI
        slicesGroup = new Group();
        root.getChildren().addAll(
                createCircle(centerX, centerY, outerRadius),
                createCircle(centerX, centerY, innerRadius),
                slicesGroup
        );
        updateSlicesAndLabels(settings.letterGroups(), -1); // No initial highlight
    }

    public void setHighlightedSection(int highlightSection) {
        for (int i = 0; i < slices.size(); i++) {
            slices.get(i).setFill(i == highlightSection ? highlightedColor : Color.color(
                    arcDefaultFillColor.getRed(),
                    arcDefaultFillColor.getGreen(),
                    arcDefaultFillColor.getBlue(),
                    arcDefaultAlpha
            ));
        }
    }

    public void setLetterGroups(String[] newLetterGroups) {
        updateSlicesAndLabels(newLetterGroups, -1);
    }

    public void updateSlicesAndLabels(String[] letterGroups, int highlightSection) {
        slices.clear();
        labelGroups.clear();
        slicesGroup.getChildren().clear();

        double angleStep = 360.0 / letterGroups.length;
        for (int i = 0; i < letterGroups.length; i++) {
            double baseStartAngle = -i * angleStep;
            double startAngle = baseStartAngle + rotationAngle;
            double endAngle = baseStartAngle - angleStep + rotationAngle;

            Path slice = createSlice(startAngle, endAngle);
            slice.setFill(i == highlightSection ? highlightedColor : Color.color(
                    arcDefaultFillColor.getRed(),
                    arcDefaultFillColor.getGreen(),
                    arcDefaultFillColor.getBlue(),
                    arcDefaultAlpha
            ));
            slicesGroup.getChildren().add(slice);
            slices.add(slice);

            Group labelGroup = createCurvedLabel(letterGroups[i], baseStartAngle, angleStep);
            slicesGroup.getChildren().add(labelGroup);
            labelGroups.add(labelGroup);
        }
    }

    private Circle createCircle(double centerX, double centerY, double radius) {
        Circle circle = new Circle(centerX, centerY, radius * scaleFactor, Color.TRANSPARENT);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(2 * scaleFactor);
        return circle;
    }

    private Path createSlice(double startAngle, double endAngle) {
        double startTheta = Math.toRadians(startAngle);
        double endTheta = Math.toRadians(endAngle);

        double[] points = calculateSlicePoints(startTheta, endTheta);
        double innerStartX = points[0], innerStartY = points[1];
        double outerStartX = points[2], outerStartY = points[3];
        double outerEndX = points[4], outerEndY = points[5];
        double innerEndX = points[6], innerEndY = points[7];

        Path slice = new Path();
        slice.getElements().add(new MoveTo(innerStartX, innerStartY));
        slice.getElements().add(new LineTo(outerStartX, outerStartY));
        ArcTo outerArc = new ArcTo();
        outerArc.setX(outerEndX);
        outerArc.setY(outerEndY);
        outerArc.setRadiusX(outerRadius * scaleFactor);
        outerArc.setRadiusY(outerRadius * scaleFactor);
        outerArc.setSweepFlag(true);
        slice.getElements().add(outerArc);
        slice.getElements().add(new LineTo(innerEndX, innerEndY));
        ArcTo innerArc = new ArcTo();
        innerArc.setX(innerStartX);
        innerArc.setY(innerStartY);
        innerArc.setRadiusX(innerRadius * scaleFactor);
        innerArc.setRadiusY(innerRadius * scaleFactor);
        innerArc.setSweepFlag(false);
        slice.getElements().add(innerArc);

        slice.setStroke(Color.GRAY);
        return slice;
    }

    private double[] calculateSlicePoints(double startTheta, double endTheta) {
        double innerStartX = innerRadius * scaleFactor * Math.cos(startTheta) + centerX;
        double innerStartY = -innerRadius * scaleFactor * Math.sin(startTheta) + centerY;
        double outerStartX = outerRadius * scaleFactor * Math.cos(startTheta) + centerX;
        double outerStartY = -outerRadius * scaleFactor * Math.sin(startTheta) + centerY;
        double outerEndX = outerRadius * scaleFactor * Math.cos(endTheta) + centerX;
        double outerEndY = -outerRadius * scaleFactor * Math.sin(endTheta) + centerY;
        double innerEndX = innerRadius * scaleFactor * Math.cos(endTheta) + centerX;
        double innerEndY = -innerRadius * scaleFactor * Math.sin(endTheta) + centerY;
        return new double[]{innerStartX, innerStartY, outerStartX, outerStartY, outerEndX, outerEndY, innerEndX, innerEndY};
    }

    private Group createCurvedLabel(String labelText, double baseStartAngle, double angleStep) {
        double labelR = (innerRadius + outerRadius) / 2 * scaleFactor;
        double midAngle = baseStartAngle - angleStep / 2 + rotationAngle;
        Group labelGroup = new Group();
        double totalCharAngle = Math.min(angleStep * 0.8, labelText.length() * 10);
        double charAngleStep = labelText.length() > 1 ? totalCharAngle / (labelText.length() - 1) : 0;
        double startCharAngle = midAngle - totalCharAngle / 2;

        for (int j = 0; j < labelText.length(); j++) {
            double charAngle = startCharAngle + j * charAngleStep;
            double charTheta = Math.toRadians(charAngle);
            double charX = labelR * Math.cos(charTheta) + centerX;
            double charY = -labelR * Math.sin(charTheta) + centerY;
            Text charText = new Text(charX, charY, String.valueOf(labelText.charAt(j)));
            charText.setFill(textColor);
            charText.setScaleX(letterSize);
            charText.setScaleY(letterSize);
            charText.setRotate(-charAngle + 90);
            labelGroup.getChildren().add(charText);
        }
        return labelGroup;
    }
}