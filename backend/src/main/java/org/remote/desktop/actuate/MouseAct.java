package org.remote.desktop.actuate;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.asmus.model.PolarCoords;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicInteger;

import static org.asmus.model.NamingConstants.MAX;
import static org.remote.desktop.util.NumUtil.mapVal;

@UtilityClass
public class MouseAct {
    double sensitivity = 0.004;
    final static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void paste() {
        click();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public static void click() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void moveMouse(PolarCoords polarCoords) {
        double scaledRadius = polarCoords.getRadius();

        scaledRadius = mapVal(scaledRadius, -32767, 32768, -10, 10);

        // Calculate Cartesian coordinates
        int xMovement = (int) (scaledRadius * Math.cos(polarCoords.getTheta()));
        int yMovement = (int) (scaledRadius * Math.sin(polarCoords.getTheta()));

        // Get the current mouse position
        int currentX = MouseInfo.getPointerInfo().getLocation().x;
        int currentY = MouseInfo.getPointerInfo().getLocation().y;

        // Move the mouse to the new position
        robot.mouseMove(currentX + xMovement, currentY + yMovement);
    }

    double sen = 0.0136D;
    public static void scrollR(PolarCoords coords) {
        double radius = coords.getRadius(); // Example value within your range
        double mappedRadius = mapVal(radius, -0, 32768, 0, 2_000_000); // One third of the range

        double y = radius * Math.sin(coords.getTheta());
        double sensitivityFactor = Math.abs(y) / Math.abs(mappedRadius); // Use mapped radius for sensitivity calculation

        int scrollAmount = (int) (Math.abs(radius) * sensitivityFactor * sen); // Adjust the 0.01 to fine-tune sensitivity

        int scrollDirection = (int) Math.signum(y); // -1 for up, 1 for down, 0 if y is 0
        robot.mouseWheel(scrollDirection * scrollAmount); // Adjust scroll amount based on sensitivity
    }

    AtomicInteger drop = new  AtomicInteger(0);
    public static void scrollWithStick(PolarCoords coords) {

        if (coords.isZero() ||  drop.incrementAndGet() < 12) {
            return;
        }

        drop.set(0);

        // Convert polar to Cartesian for easier interpretation
        double x = coords.getRadius() * Math.cos(coords.getTheta());
        double y = coords.getRadius() * Math.sin(coords.getTheta());

        // Normalize x to [-1, 1] range (max r = 32767)
        double normalizedX = x / MAX;
        double normalizedY = y / MAX;

        // Determine scroll direction based on y-axis
        // Positive y (stick up) = scroll up (negative wheel amount)
        // Negative y (stick down) = scroll down (positive wheel amount)
//        int scrollDirection = normalizedY < 0.1 ? -1 : (normalizedY > -0.1 ? 1 : 0);
        int scrollDirection = (int) Math.signum(y); // -1 for up, 1 for down, 0 if y is 0

        // Map x-axis to wheelAmount (1 to 7)
        // Left (x < 0) -> lower intensity, Right (x > 0) -> higher intensity
        double absNormalizedX = Math.abs(normalizedX);
        // Linearly map absNormalizedX [0, 1] to wheelAmount [1, 7]
        int wheelAmount = (int) Math.round(1 + (absNormalizedX * 4));
        wheelAmount = Math.max(1, Math.min(4, wheelAmount)); // Clamp to [1, 7]

        // Apply scroll if there's a direction
        if (scrollDirection != 0) {
            robot.mouseWheel(scrollDirection * wheelAmount);
        }
    }

    int delay = 3; // milliseconds between each scroll step
    @SneakyThrows
    public static void scroll(PolarCoords coords) {
        double theta = coords.getTheta(); // 45 degrees in radians
        double radius = coords.getRadius(); // Example value within your range

        double x = radius * Math.cos(theta);
        double y = radius * Math.sin(theta);

        // Adjust scroll sensitivity based on both radius and angle
        double mappedRadius = mapVal(radius, -32767, 32768, -1_560_000, 1_560_000); // One third of the range
        double sensitivityFactor = Math.abs(y) / Math.abs(mappedRadius); // Use mapped radius for sensitivity calculation

        int scrollAmount = (int) (Math.abs(radius) * sensitivityFactor * 0.002); // Adjust the 0.01 to fine-tune sensitivity

        // Vertical Scrolling
        // Use y to determine scroll direction and magnitude
        int scrollDirection = (int) Math.signum(y); // -1 for up, 1 for down, 0 if y is 0
        robot.mouseWheel(scrollDirection * scrollAmount); // Adjust scroll amount based on sensitivity
        Thread.sleep(delay);
    }


}
