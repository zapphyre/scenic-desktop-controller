package org.remote.desktop.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EQualificationType;

import java.util.Set;

@With
@Value
@Builder
@EqualsAndHashCode
public class ButtonActionDef {

    EButtonAxisMapping trigger;
    Set<EButtonAxisMapping> modifiers;
    boolean longPress;
    EQualificationType qualified;
}
