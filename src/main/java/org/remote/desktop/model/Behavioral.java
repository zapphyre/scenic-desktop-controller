package org.remote.desktop.model;

import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;

import java.util.Set;

public interface Behavioral {
    default boolean hasModifiersAssigned() {
        return !getModifiers().isEmpty();
    }

    default boolean hasClickMultiplicity() {
        return getMultiplicity().ordinal() > 0;
    }

    boolean isLongPress();

    Set<EButtonAxisMapping> getModifiers();

    EMultiplicity getMultiplicity();
}
