package org.remote.desktop.ui.component;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import org.remote.desktop.ui.SegmentSelectable;

import java.util.LinkedList;
import java.util.List;

public class LetterCircle extends Pane implements SegmentSelectable {
    private final List<Path> slices = new LinkedList<>();
    private final List<Group> labelGroups = new LinkedList<>();
    private final double letterSize;
    private final Color arcDefaultFillColor;
    private final double arcDefaultAlpha;
    private final Color highlightedColor;
    private final Color textColor;
    private final double bezelScale;

    private final Group slicesGroup = new Group();
    private final double centerX, centerY;
    private final double scaleFactor, innerRadius, outerRadius, rotationAngle;

    public LetterCircle(
            double letterSize,
            Color arcDefaultFillColor,
            double arcDefaultAlpha,
            Color highlightedColor,
            Color textColor,
            double bezelScale,
            double scaleFactor,
            int highlightSection,
            double innerRadius,
            double outerRadius,
            String[] letterGroups,
            double rotationAngle
    ) {
        this.letterSize = letterSize;
        this.arcDefaultFillColor = arcDefaultFillColor;
        this.arcDefaultAlpha = Math.max(0.0, Math.min(1.0, arcDefaultAlpha));
        this.highlightedColor = highlightedColor;
        this.textColor = textColor;
        this.bezelScale = Math.max(0.0, Math.min(1.0, bezelScale));
        this.scaleFactor = scaleFactor;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.rotationAngle = rotationAngle;

        double bezelDepth = 5 * scaleFactor * bezelScale;
        double totalRadius = outerRadius * scaleFactor + bezelDepth;
        double paneSize = 2 * totalRadius;
        this.centerX = this.centerY = totalRadius;

        setPrefSize(paneSize, paneSize);
        getChildren().addAll(
                createCircle(centerX, centerY, outerRadius),
                createCircle(centerX, centerY, innerRadius),
                slicesGroup
        );

        updateSlicesAndLabels(letterGroups, highlightSection);
    }

    public void selectSegment(int segment) {
        for (int i = 0; i < slices.size(); i++) {
            Color baseColor = (i == segment) ? highlightedColor : Color.color(
                    arcDefaultFillColor.getRed(),
                    arcDefaultFillColor.getGreen(),
                    arcDefaultFillColor.getBlue(),
                    arcDefaultAlpha
            );
            slices.get(i).setFill(createMainGradient(baseColor, i == segment));
        }
    }

    public void setLetterGroups(String[] newLetterGroups) {
        updateSlicesAndLabels(newLetterGroups, -1);
    }

    private void updateSlicesAndLabels(String[] letterGroups, int highlightSection) {
        slices.clear();
        labelGroups.clear();
        slicesGroup.getChildren().clear();

        double angleStep = 360.0 / letterGroups.length;

        for (int i = 0; i < letterGroups.length; i++) {
            double baseAngle = -i * angleStep;
            double startAngle = baseAngle + rotationAngle;
            double endAngle = baseAngle - angleStep + rotationAngle;

            Group sliceGroup = createSliceWith3D(startAngle, endAngle, i == highlightSection);
            Group labelGroup = createCurvedLabel(letterGroups[i], baseAngle, angleStep);

            slices.add((Path) sliceGroup.getChildren().get(1));
            labelGroups.add(labelGroup);
            slicesGroup.getChildren().addAll(sliceGroup, labelGroup);
        }
    }

    private Circle createCircle(double cx, double cy, double r) {
        Circle circle = new Circle(cx, cy, r * scaleFactor, Color.TRANSPARENT);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(2 * scaleFactor);
        return circle;
    }

    private Group createSliceWith3D(double startAngle, double endAngle, boolean isHighlighted) {
        double startRad = Math.toRadians(startAngle);
        double endRad = Math.toRadians(endAngle);
        double[] p = calculateSlicePoints(startRad, endRad);

        Path mainSlice = new Path(
                new MoveTo(p[0], p[1]),
                new LineTo(p[2], p[3]),
                createArcTo(p[4], p[5], outerRadius * scaleFactor, true),
                new LineTo(p[6], p[7]),
                createArcTo(p[0], p[1], innerRadius * scaleFactor, false)
        );
        Color fillColor = isHighlighted ? highlightedColor : Color.color(
                arcDefaultFillColor.getRed(),
                arcDefaultFillColor.getGreen(),
                arcDefaultFillColor.getBlue(),
                arcDefaultAlpha
        );
        mainSlice.setFill(createMainGradient(fillColor, isHighlighted));
        mainSlice.setStroke(Color.GRAY);

        double bezelDepth = 5 * scaleFactor * bezelScale;
        Path innerBezel = createShadowPath(startRad, endRad, innerRadius * scaleFactor + bezelDepth, outerRadius * scaleFactor - bezelDepth);
        Path outerBezel = createShadowPath(startRad, endRad, innerRadius * scaleFactor - bezelDepth, outerRadius * scaleFactor + bezelDepth);

        innerBezel.setFill(createBezelGradient(Color.color(0, 0, 0, 0.3), true));
        outerBezel.setFill(createBezelGradient(Color.color(0, 0, 0, 0.2), false));

        return new Group(innerBezel, mainSlice, outerBezel);
    }

    private ArcTo createArcTo(double x, double y, double radius, boolean sweep) {
        ArcTo arc = new ArcTo();
        arc.setX(x);
        arc.setY(y);
        arc.setRadiusX(radius);
        arc.setRadiusY(radius);
        arc.setSweepFlag(sweep);
        return arc;
    }

    private Path createShadowPath(double start, double end, double innerR, double outerR) {
        double[] p = calculateShadowPoints(start, end, innerR, outerR);
        return new Path(
                new MoveTo(p[0], p[1]),
                new LineTo(p[2], p[3]),
                createArcTo(p[4], p[5], outerR, true),
                new LineTo(p[6], p[7]),
                createArcTo(p[0], p[1], innerR, false)
        );
    }

    private RadialGradient createMainGradient(Color baseColor, boolean highlighted) {
        return new RadialGradient(
                0, 0, centerX, centerY, outerRadius * scaleFactor, false, CycleMethod.NO_CYCLE,
                new Stop[]{
                        new Stop(0.0, baseColor),
                        new Stop(0.7, baseColor),
                        new Stop(1.0, highlighted ? baseColor.darker().darker() : baseColor.darker())
                }
        );
    }

    private RadialGradient createBezelGradient(Color baseColor, boolean isInner) {
        Stop[] stops = isInner
                ? new Stop[]{new Stop(0.0, baseColor.darker()), new Stop(1.0, baseColor.brighter())}
                : new Stop[]{new Stop(0.0, baseColor.brighter()), new Stop(1.0, baseColor.darker())};
        return new RadialGradient(0, 0, centerX, centerY, outerRadius * scaleFactor, false, CycleMethod.NO_CYCLE, stops);
    }

    private double[] calculateSlicePoints(double start, double end) {
        return new double[]{
                innerRadius * scaleFactor * Math.cos(start) + centerX,
                -innerRadius * scaleFactor * Math.sin(start) + centerY,
                outerRadius * scaleFactor * Math.cos(start) + centerX,
                -outerRadius * scaleFactor * Math.sin(start) + centerY,
                outerRadius * scaleFactor * Math.cos(end) + centerX,
                -outerRadius * scaleFactor * Math.sin(end) + centerY,
                innerRadius * scaleFactor * Math.cos(end) + centerX,
                -innerRadius * scaleFactor * Math.sin(end) + centerY
        };
    }

    private double[] calculateShadowPoints(double start, double end, double r1, double r2) {
        return new double[]{
                r1 * Math.cos(start) + centerX,
                -r1 * Math.sin(start) + centerY,
                r2 * Math.cos(start) + centerX,
                -r2 * Math.sin(start) + centerY,
                r2 * Math.cos(end) + centerX,
                -r2 * Math.sin(end) + centerY,
                r1 * Math.cos(end) + centerX,
                -r1 * Math.sin(end) + centerY
        };
    }

    private Group createCurvedLabel(String text, double baseAngle, double angleStep) {
        double radius = (innerRadius + outerRadius) / 2 * scaleFactor;
        double midAngle = baseAngle - angleStep / 2 + rotationAngle;
        Group labelGroup = new Group();

        double totalAngle = Math.min(angleStep * 0.8, text.length() * 10);
        double step = text.length() > 1 ? totalAngle / (text.length() - 1) : 0;
        double startAngle = midAngle + totalAngle / 2;

        for (int i = 0; i < text.length(); i++) {
            double angle = startAngle - i * step;
            double theta = Math.toRadians(angle);
            double x = radius * Math.cos(theta) + centerX;
            double y = -radius * Math.sin(theta) + centerY;

            Text letter = new Text(x, y, String.valueOf(text.charAt(i)));
            letter.setFill(textColor);
            letter.setScaleX(letterSize);
            letter.setScaleY(letterSize);
            letter.setRotate(-angle - 90);
            labelGroup.getChildren().add(letter);
        }

        return labelGroup;
    }
}
