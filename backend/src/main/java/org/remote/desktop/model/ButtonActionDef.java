package org.remote.desktop.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.EMultiplicity;
import org.asmus.model.EQualificationType;

import java.util.Set;

@With
@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ButtonActionDef {

    @EqualsAndHashCode.Include
    String trigger;
    ELogicalTrigger logicalTrigger;
    ELogicalEventType logicalEventType;

    // both modifiers and longPress has to be excluded from equality, b/c filtering of leftover qualified events need NOT
    // to account for them; it only need to account about button's itself identity
    Set<EButtonAxisMapping> modifiers;
    boolean longPress;

    @EqualsAndHashCode.Include
    EQualificationType qualified;

    EMultiplicity multiplicity;

    int position;
}
