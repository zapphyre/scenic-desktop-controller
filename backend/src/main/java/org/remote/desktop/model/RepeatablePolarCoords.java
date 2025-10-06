package org.remote.desktop.model;

import lombok.experimental.SuperBuilder;
import org.asmus.model.GamepadDevice;
import org.asmus.model.PolarCoords;

@SuperBuilder
public class RepeatablePolarCoords extends PolarCoords implements Repeatable {

    public RepeatablePolarCoords(double radius, double theta, GamepadDevice device) {
        super(radius, theta, device);
    }

    @Override
    public boolean isRepeatable() {
        return !this.isZero();
    }
}
