package org.remote.desktop.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EQualificationType;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ButtonActionDef {

    @EqualsAndHashCode.Include
    EButtonAxisMapping trigger;

    Set<EButtonAxisMapping> modifiers;
    boolean longPress;

    @EqualsAndHashCode.Exclude
    EQualificationType qualified;
}
