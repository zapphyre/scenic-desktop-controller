package org.remote.desktop.ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.List;

public class CircleWidget {

    private final List<Path> slices = new LinkedList<>(); // Store slices for runtime updates

    public Scene createScene(double scaleFactor, int highlightSection, double innerRadius, double outerRadius, String[] letterGroups, double rotationAngle) {
        Pane root = new Pane();
        double centerX = 150 * scaleFactor, centerY = 100 * scaleFactor;

        Circle outerCircle = new Circle(centerX, centerY, outerRadius * scaleFactor, Color.TRANSPARENT);
        outerCircle.setStroke(Color.WHITE);
        outerCircle.setStrokeWidth(2 * scaleFactor);

        Circle innerCircle = new Circle(centerX, centerY, innerRadius * scaleFactor, Color.TRANSPARENT);
        innerCircle.setStroke(Color.WHITE);
        innerCircle.setStrokeWidth(2 * scaleFactor);

        Group slicesGroup = new Group();
        double angleStep = 360.0 / letterGroups.length;

        for (int i = 0; i < letterGroups.length; i++) {
            double baseStartAngle = -i * angleStep;
            double startAngle = baseStartAngle + rotationAngle;
            double endAngle = baseStartAngle - angleStep + rotationAngle;

            double startTheta = Math.toRadians(startAngle);
            double endTheta = Math.toRadians(endAngle);

            double innerStartX = innerRadius * scaleFactor * Math.cos(startTheta) + centerX;
            double innerStartY = -innerRadius * scaleFactor * Math.sin(startTheta) + centerY;
            double outerStartX = outerRadius * scaleFactor * Math.cos(startTheta) + centerX;
            double outerStartY = -outerRadius * scaleFactor * Math.sin(startTheta) + centerY;
            double outerEndX = outerRadius * scaleFactor * Math.cos(endTheta) + centerX;
            double outerEndY = -outerRadius * scaleFactor * Math.sin(endTheta) + centerY;
            double innerEndX = innerRadius * scaleFactor * Math.cos(endTheta) + centerX;
            double innerEndY = -innerRadius * scaleFactor * Math.sin(endTheta) + centerY;

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

            slice.setFill(i == highlightSection ? Color.GREEN : Color.TRANSPARENT);
            slice.setStroke(Color.GRAY);
            slicesGroup.getChildren().add(slice);
            slices.add(slice); // Store for runtime updates

            double labelR = (innerRadius + outerRadius) / 2 * scaleFactor;
            double labelTheta = Math.toRadians(baseStartAngle - angleStep / 2 + rotationAngle);
            double labelX = labelR * Math.cos(labelTheta) + centerX - 10 * scaleFactor;
            double labelY = -labelR * Math.sin(labelTheta) + centerY + 5 * scaleFactor;
            Text groupLabel = new Text(labelX, labelY, letterGroups[i]);
            groupLabel.setFill(Color.WHITE);
            groupLabel.setScaleX(scaleFactor);
            groupLabel.setScaleY(scaleFactor);
            slicesGroup.getChildren().add(groupLabel);
        }

        root.getChildren().addAll(outerCircle, innerCircle, slicesGroup);
        Scene scene = new Scene(root, 2 * (2 * outerRadius * scaleFactor + 50 * scaleFactor), 200 * scaleFactor);
        scene.setFill(null);
        return scene;
    }

    // Method to update the highlighted section in runtime
    public void setHighlightedSection(int highlightSection) {
        for (int i = 0; i < slices.size(); i++) {
            slices.get(i).setFill(i == highlightSection ? Color.GREEN : Color.TRANSPARENT);
        }
    }
}