package org.remote.desktop.text.translator;

public class PolarSectionTranslatorFactory {

    public static PolarCoordsSectionTranslator createTranslator(PolarSettings settings) {
        return coords -> {  
            // Convert theta from radians (-π to π) to degrees (0° to 360°)
            double thetaDegrees = Math.toDegrees(coords.getTheta());
            if (thetaDegrees < 0) thetaDegrees += 360.0;

            // Normalize to 0-360°
            thetaDegrees = thetaDegrees % 360.0;
            if (thetaDegrees < 0) thetaDegrees += 360.0;

            // Adjust for rotationAngle (clockwise, matching CircleWidget)
            double adjustedTheta = (thetaDegrees - settings.rotationAngle()) % 360.0;
            if (adjustedTheta < 0) adjustedTheta += 360.0;

            // Calculate section
            double anglePerSection = 360.0 / settings.numberOfSections();
            int section = (int) Math.floor(adjustedTheta / anglePerSection);

            // Ensure section is within 0 to numberOfSections-1
            return section % settings.numberOfSections();
        };
    }
}
