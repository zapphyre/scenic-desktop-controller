package org.remote.desktop.actuate;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.asmus.model.PolarCoords;

import java.awt.*;
import java.awt.event.InputEvent;

@UtilityClass
public class MouseCtrl {
    double sensitivity = 0.002;
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
        double scaledRadius = polarCoords.radius() * sensitivity ;

        // Calculate Cartesian coordinates
        int xMovement = (int) (scaledRadius * Math.cos(polarCoords.theta()));
        int yMovement = (int) (scaledRadius * Math.sin(polarCoords.theta()));

        // Get the current mouse position
        int currentX = MouseInfo.getPointerInfo().getLocation().x;
        int currentY = MouseInfo.getPointerInfo().getLocation().y;

        // Move the mouse to the new position
        robot.mouseMove(currentX + xMovement, currentY + yMovement);
    }

    @SneakyThrows
    public static void scroll(PolarCoords coords) {
        double theta = coords.theta(); // 45 degrees in radians
        double radius = coords.radius(); // Example value within your range

        double x = radius * Math.cos(theta);
        double y = radius * Math.sin(theta);

        // Adjust scroll sensitivity based on both radius and angle
        double mappedRadius = mapRadius(radius, -32767, 32768, -1_560_000, 1_560_000); // One third of the range
        double sensitivityFactor = Math.abs(y) / Math.abs(mappedRadius); // Use mapped radius for sensitivity calculation

        int scrollAmount = (int) (Math.abs(radius) * sensitivityFactor * 0.002); // Adjust the 0.01 to fine-tune sensitivity
        int delay = 21; // milliseconds between each scroll step

        // Vertical Scrolling
        // Use y to determine scroll direction and magnitude
        int scrollDirection = (int) Math.signum(y); // -1 for up, 1 for down, 0 if y is 0
        robot.mouseWheel(scrollDirection * scrollAmount); // Adjust scroll amount based on sensitivity
        Thread.sleep(delay);
    }

    private static double mapRadius(double value, int start1, int stop1, int start2, int stop2) {
        return start2 + (stop2 - start2) * (value - start1) / (stop1 - start1);
    }
}
