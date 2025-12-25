package org.remote.desktop.actuate;

import jxdotool.YdoToolUtil;
import org.asmus.model.PolarCoords;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import static jxdotool.YdoToolUtil.moveMousePolar;
import static org.remote.desktop.util.NumUtil.mapVal;

@Primary
@Service
public class YdoMouseAct implements PointingService {

    @Override
    public void paste() {

    }

    @Override
    public void click() {

    }

    @Override
    public void moveMouse(PolarCoords polarCoords) {
        double rawRadius = polarCoords.getRadius();  // ~0 to 32_000+

        // Scale exactly like your old working code: full stick → ±10 "units"
        double scaledRadius = mapVal(rawRadius, 0, 32768, 0, 6);  // only positive for magnitude

        // Optional deadzone
        if (scaledRadius < 1.0) {
            return;  // skip tiny noise
        }

        // Optional acceleration (makes small movements precise, full push faster)
        scaledRadius = Math.pow(scaledRadius / 10.0, 1.4) * 10.0;

        // Compute cartesian deltas (same as before)
        int dx = (int) Math.round(scaledRadius * Math.cos(polarCoords.getTheta()));
        int dy = (int) Math.round(scaledRadius * Math.sin(polarCoords.getTheta()));

        // Skip if no movement
        if (dx == 0 && dy == 0) {
            return;
        }

        // Use ydotool relative cartesian move (fast, smooth, no clamping issues)
        YdoToolUtil.moveMouse(dx, dy);
    }

    @Override
    public void scrollWithStick(PolarCoords coords) {
        double rawRadius = coords.getRadius();  // 0 to ~32_000
        double thetaRad = coords.getTheta();

//        System.out.println("rawRadius: " + rawRadius + " thetaRad: " + thetaRad);

        // Scale magnitude: full stick = strong scroll
        double scaledMagnitude = mapVal(rawRadius, 0, 32768, 0, 2);  // 0 to 8 "units"

        // Optional: acceleration curve for better feel
        scaledMagnitude = Math.pow(scaledMagnitude / 8.0, 1.3) * 8.0;

        int magnitude = (int) Math.round(scaledMagnitude);

        if (magnitude == 0)
            return;

        // Compute direction components
        double dx = magnitude * Math.cos(thetaRad);  // right > 0
        double dy = magnitude * Math.sin(thetaRad);  // down > 0

        int scrollX = (int) Math.round(dx);  // horizontal scroll
        int scrollY = (int) Math.round(-dy);  // vertical scroll (positive = down)

        // Invert Y for ydotool (we want positive dy = scroll up)
        YdoToolUtil.scroll(scrollX, -scrollY);
    }
}
