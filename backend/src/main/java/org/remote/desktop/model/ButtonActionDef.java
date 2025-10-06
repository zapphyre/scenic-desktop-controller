package org.remote.desktop.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.asmus.model.*;

import java.util.HashSet;
import java.util.Set;

@With
@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ButtonActionDef implements Repeatable {

    @EqualsAndHashCode.Include
    String trigger;
    ELogicalTrigger logicalTrigger;
    ELogicalEventType logicalEventType;

    // both modifiers and longPress has to be excluded from equality, b/c filtering of leftover qualified events need NOT
    // to account for them; it only needs to account about button's identity itself
    @Builder.Default
    Set<EButtonAxisMapping> modifiers = new HashSet<>();
    boolean longPress;

    @EqualsAndHashCode.Include
    EQualificationType qualified;

    EMultiplicity multiplicity;

    int position;

    GamepadDevice device;

    @Override
    public boolean isRepeatable() {
        return logicalEventType.equals(ELogicalEventType.STEP_POSITIVE) ||
                logicalEventType.equals(ELogicalEventType.STEP_NEGATIVE);
    }
}
