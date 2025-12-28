package org.remote.desktop.actuate;

import jxdotool.YdoToolUtil;
import org.asmus.model.PolarCoords;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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

    private static final long SCROLL_COOLDOWN_MS = 111; // 500 ms
    private volatile long lastScrollTime = 0;

    public void scrollWithStick(PolarCoords coords) {
        double rawRadius = coords.getRadius();
        double thetaRad = coords.getTheta();

        long now = System.currentTimeMillis();

        if (now - lastScrollTime < SCROLL_COOLDOWN_MS) return;

        lastScrollTime = now;

        double scaledMagnitude = mapVal(rawRadius, 0, 32768, 0, 8);  // 0 to 8 "units"

        scaledMagnitude = Math.pow(scaledMagnitude / 8.0, 1.3) * 8.0;

        int magnitude = (int) Math.round(scaledMagnitude);

        if (magnitude == 0) return;

        double dx = magnitude * Math.cos(thetaRad);
        double dy = magnitude * Math.sin(thetaRad);

        int scrollX = (int) Math.round(dx);
        int scrollY = (int) Math.round(-dy);

        YdoToolUtil.scroll(scrollX, -scrollY);
    }
}
