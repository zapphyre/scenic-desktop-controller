package org.remote.desktop.model;

import lombok.experimental.SuperBuilder;
import org.asmus.model.PolarCoords;

@SuperBuilder
public class RepeatablePolarCoords extends PolarCoords implements Repeatable {

    public RepeatablePolarCoords(double radius, double theta) {
        super(radius, theta);
    }

    @Override
    public boolean isRepeatable() {
        return !this.isZero();
    }
}
