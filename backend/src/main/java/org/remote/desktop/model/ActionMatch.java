package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder
public class ActionMatch {
    String trigger;
    @Builder.Default
    Set<EButtonAxisMapping> modifiers = new HashSet<>();

    @Builder.Default
    EMultiplicity multiplicity = EMultiplicity.CLICK;
    boolean longPress;
}
