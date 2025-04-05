package org.remote.desktop.model;

import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;

import java.util.Optional;
import java.util.Set;

public interface Behavioral {
    default boolean hasModifiersAssigned() {
        return !getModifiers().isEmpty();
    }

    default boolean hasClickMultiplicity() {
        return Optional.ofNullable(getMultiplicity())
                .orElse(EMultiplicity.CLICK)
                .ordinal() > 0;
    }

    boolean isLongPress();

    default boolean isPushAction() {
        return !hasClickMultiplicity() && !hasModifiersAssigned();
    }

    Set<EButtonAxisMapping> getModifiers();

    EMultiplicity getMultiplicity();
}
