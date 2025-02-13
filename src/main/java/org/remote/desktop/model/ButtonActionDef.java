package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;

import java.util.Set;

@Value
@Builder
public class ButtonActionDef {
    EButtonAxisMapping trigger;
    Set<EButtonAxisMapping> modifiers;
    boolean longPress;
}
