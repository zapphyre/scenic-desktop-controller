package org.remote.desktop.actuate;

import lombok.experimental.UtilityClass;
import org.asmus.model.PolarCoords;

import java.awt.*;
import java.awt.event.InputEvent;

@UtilityClass
public class MouseCtrl {
    double sensitivity = 0.004;
    final static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void click() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }


    public static void moveMouse(PolarCoords polarCoords) {
        double scaledRadius = polarCoords.getRadius() * sensitivity;

        // Calculate Cartesian coordinates
        int xMovement = (int) (scaledRadius * Math.cos(polarCoords.getTheta()));
        int yMovement = (int) (scaledRadius * Math.sin(polarCoords.getTheta()));

        // Get the current mouse position
        int currentX = MouseInfo.getPointerInfo().getLocation().x;
        int currentY = MouseInfo.getPointerInfo().getLocation().y;

        // Move the mouse to the new position
        robot.mouseMove(currentX + xMovement, currentY + yMovement);
    }
}
