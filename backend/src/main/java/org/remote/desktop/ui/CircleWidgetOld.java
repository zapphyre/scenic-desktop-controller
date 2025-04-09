package org.remote.desktop.ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import java.util.LinkedList;
import java.util.List;

public class CircleWidgetOld {
    private final List<Path> slices = new LinkedList<>();
    private final List<Group> labelGroups = new LinkedList<>();
    private double scaleFactor;
    private double innerRadius;
    private double outerRadius;
    private double rotationAngle;
    private double centerX;
    private double centerY;
    private Pane root;
    private Group slicesGroup;
    private final double letterSize;
    private final Color arcDefaultFillColor;
    private final double arcDefaultAlpha;
    private final Color highlightedColor;
    private final Color textColor;
    private final double bezelScale;

    public CircleWidgetOld(double letterSize, Color arcDefaultFillColor, double arcDefaultAlpha, Color highlightedColor, Color textColor, double bezelScale) {
        this.letterSize = letterSize;
        this.arcDefaultFillColor = arcDefaultFillColor;
        this.arcDefaultAlpha = Math.max(0.0, Math.min(1.0, arcDefaultAlpha));
        this.highlightedColor = highlightedColor;
        this.textColor = textColor;
        this.bezelScale = Math.max(0.0, Math.min(1.0, bezelScale));
    }

    public Scene createScene(double scaleFactor, int highlightSection, double innerRadius, double outerRadius, String[] letterGroups, double rotationAngle) {
        this.scaleFactor = scaleFactor;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.rotationAngle = rotationAngle;

        // Compute the size including the outer bezel
        double bezelDepth = 5 * scaleFactor * bezelScale;
        double totalRadius = outerRadius * scaleFactor + bezelDepth; // Total radius including bezel
        double paneSize = 2 * totalRadius; // Width and height of the pane to fit the circle + bezel
        this.centerX = totalRadius; // Center within the pane
        this.centerY = totalRadius;

        root = new Pane();
        root.setPrefSize(paneSize, paneSize); // Set the pane size to fit the circle + bezel
        root.getChildren().addAll(createCircle(centerX, centerY, outerRadius), createCircle(centerX, centerY, innerRadius));
        slicesGroup = new Group();
        updateSlicesAndLabels(letterGroups, highlightSection);

        root.getChildren().add(slicesGroup);
        Scene scene = new Scene(root, paneSize, paneSize); // Scene matches the pane size
        scene.setFill(null);
        return scene;
    }

    public void setHighlightedSection(int highlightSection) {
        for (int i = 0; i < slices.size(); i++) {
            Path slice = slices.get(i);
            slice.setFill(i == highlightSection ? createMainGradient(highlightedColor, true) : createMainGradient(Color.color(
                    arcDefaultFillColor.getRed(),
                    arcDefaultFillColor.getGreen(),
                    arcDefaultFillColor.getBlue(),
                    arcDefaultAlpha
            ), false));
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

            Group sliceGroup = createSliceWith3D(startAngle, endAngle, i == highlightSection);
            slicesGroup.getChildren().add(sliceGroup);
            slices.add((Path) sliceGroup.getChildren().get(1));

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

    private Group createSliceWith3D(double startAngle, double endAngle, boolean isHighlighted) {
        double startTheta = Math.toRadians(startAngle);
        double endTheta = Math.toRadians(endAngle);
        double[] points = calculateSlicePoints(startTheta, endTheta);

        double innerStartX = points[0], innerStartY = points[1];
        double outerStartX = points[2], outerStartY = points[3];
        double outerEndX = points[4], outerEndY = points[5];
        double innerEndX = points[6], innerEndY = points[7];

        Path mainSlice = new Path();
        mainSlice.getElements().add(new MoveTo(innerStartX, innerStartY));
        mainSlice.getElements().add(new LineTo(outerStartX, outerStartY));
        ArcTo outerArc = new ArcTo();
        outerArc.setX(outerEndX);
        outerArc.setY(outerEndY);
        outerArc.setRadiusX(outerRadius * scaleFactor);
        outerArc.setRadiusY(outerRadius * scaleFactor);
        outerArc.setSweepFlag(true);
        mainSlice.getElements().add(outerArc);
        mainSlice.getElements().add(new LineTo(innerEndX, innerEndY));
        ArcTo innerArc = new ArcTo();
        innerArc.setX(innerStartX);
        innerArc.setY(innerStartY);
        innerArc.setRadiusX(innerRadius * scaleFactor);
        innerArc.setRadiusY(innerRadius * scaleFactor);
        innerArc.setSweepFlag(false);
        mainSlice.getElements().add(innerArc);
        mainSlice.setFill(isHighlighted ? createMainGradient(highlightedColor, true) : createMainGradient(Color.color(
                arcDefaultFillColor.getRed(),
                arcDefaultFillColor.getGreen(),
                arcDefaultFillColor.getBlue(),
                arcDefaultAlpha
        ), false));
        mainSlice.setStroke(Color.GRAY);

        double bezelDepth = 5 * scaleFactor * bezelScale;
        Path innerBezel = createShadowPath(startTheta, endTheta, innerRadius * scaleFactor + bezelDepth, outerRadius * scaleFactor - bezelDepth);
        innerBezel.setFill(createBezelGradient(Color.color(0, 0, 0, 0.3), true));
        innerBezel.setStroke(null);

        Path outerBezel = createShadowPath(startTheta, endTheta, innerRadius * scaleFactor - bezelDepth, outerRadius * scaleFactor + bezelDepth);
        outerBezel.setFill(createBezelGradient(Color.color(0, 0, 0, 0.2), false));
        outerBezel.setStroke(null);

        Group sliceGroup = new Group(innerBezel, mainSlice, outerBezel);
        return sliceGroup;
    }

    private Path createShadowPath(double startTheta, double endTheta, double shadowInnerRadius, double shadowOuterRadius) {
        double[] shadowPoints = calculateShadowPoints(startTheta, endTheta, shadowInnerRadius, shadowOuterRadius);
        double innerStartX = shadowPoints[0], innerStartY = shadowPoints[1];
        double outerStartX = shadowPoints[2], outerStartY = shadowPoints[3];
        double outerEndX = shadowPoints[4], outerEndY = shadowPoints[5];
        double innerEndX = shadowPoints[6], innerEndY = shadowPoints[7];

        Path shadow = new Path();
        shadow.getElements().add(new MoveTo(innerStartX, innerStartY));
        shadow.getElements().add(new LineTo(outerStartX, outerStartY));
        ArcTo outerArc = new ArcTo();
        outerArc.setX(outerEndX);
        outerArc.setY(outerEndY);
        outerArc.setRadiusX(shadowOuterRadius);
        outerArc.setRadiusY(shadowOuterRadius);
        outerArc.setSweepFlag(true);
        shadow.getElements().add(outerArc);
        shadow.getElements().add(new LineTo(innerEndX, innerEndY));
        ArcTo innerArc = new ArcTo();
        innerArc.setX(innerStartX);
        innerArc.setY(innerStartY);
        innerArc.setRadiusX(shadowInnerRadius);
        innerArc.setRadiusY(shadowInnerRadius);
        innerArc.setSweepFlag(false);
        shadow.getElements().add(innerArc);

        return shadow;
    }

    private RadialGradient createMainGradient(Color baseColor, boolean isHighlighted) {
        Stop[] stops = new Stop[]{
                new Stop(0.0, baseColor),
                new Stop(0.7, baseColor),
                new Stop(1.0, isHighlighted ? baseColor.darker().darker() : baseColor.darker())
        };
        return new RadialGradient(
                0, 0, centerX, centerY, outerRadius * scaleFactor, false, CycleMethod.NO_CYCLE, stops
        );
    }

    private RadialGradient createBezelGradient(Color baseColor, boolean isInner) {
        Stop[] stops = isInner ? new Stop[]{
                new Stop(0.0, baseColor.darker()),
                new Stop(1.0, baseColor.brighter())
        } : new Stop[]{
                new Stop(0.0, baseColor.brighter()),
                new Stop(1.0, baseColor.darker())
        };
        return new RadialGradient(
                0, 0, centerX, centerY, outerRadius * scaleFactor, false, CycleMethod.NO_CYCLE, stops
        );
    }

    private double[] calculateShadowPoints(double startTheta, double endTheta, double shadowInnerRadius, double shadowOuterRadius) {
        double innerStartX = shadowInnerRadius * Math.cos(startTheta) + centerX;
        double innerStartY = -shadowInnerRadius * Math.sin(startTheta) + centerY;
        double outerStartX = shadowOuterRadius * Math.cos(startTheta) + centerX;
        double outerStartY = -shadowOuterRadius * Math.sin(startTheta) + centerY;
        double outerEndX = shadowOuterRadius * Math.cos(endTheta) + centerX;
        double outerEndY = -shadowOuterRadius * Math.sin(endTheta) + centerY;
        double innerEndX = shadowInnerRadius * Math.cos(endTheta) + centerX;
        double innerEndY = -shadowInnerRadius * Math.sin(endTheta) + centerY;
        return new double[]{innerStartX, innerStartY, outerStartX, outerStartY, outerEndX, outerEndY, innerEndX, innerEndY};
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
        double midAngle = baseStartAngle - angleStep / 2 + rotationAngle; // Center of the arc
        Group labelGroup = new Group();
        double totalCharAngle = Math.min(angleStep * 0.8, labelText.length() * 10); // Total angle span for text
        double charAngleStep = labelText.length() > 1 ? totalCharAngle / (labelText.length() - 1) : 0;
        double startCharAngle = midAngle + totalCharAngle / 2; // Start from the left (clockwise)

        for (int j = 0; j < labelText.length(); j++) {
            double charAngle = startCharAngle - j * charAngleStep; // Decrease angle clockwise for left-to-right
            double charTheta = Math.toRadians(charAngle);
            double charX = labelR * Math.cos(charTheta) + centerX;
            double charY = -labelR * Math.sin(charTheta) + centerY;
            Text charText = new Text(charX, charY, String.valueOf(labelText.charAt(j)));
            charText.setFill(textColor);
            charText.setScaleX(letterSize);
            charText.setScaleY(letterSize);
            charText.setRotate(-charAngle - 90); // Adjust rotation for readability (clockwise)
            labelGroup.getChildren().add(charText);
        }
        return labelGroup;
    }
}