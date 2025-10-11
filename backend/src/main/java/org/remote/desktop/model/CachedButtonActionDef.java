package org.remote.desktop.model;

import lombok.Value;
import org.asmus.model.*;

import java.util.HashSet;
import java.util.Set;

@Value
public class CachedButtonActionDef {

    String trigger;
    ELogicalTrigger logicalTrigger;
    ELogicalEventType logicalEventType;

    Set<EButtonAxisMapping> modifiers = new HashSet<>();
    boolean longPress;
    EQualificationType qualified;
    EMultiplicity multiplicity;

    GamepadDevice device;
}
