package org.remote.desktop.text.translator;

public record PolarSettings(double rotationAngle, int numberOfSections) {
    public PolarSettings {
        if (numberOfSections <= 0) {
            throw new IllegalArgumentException("Number of sections must be positive");
        }
    }
}