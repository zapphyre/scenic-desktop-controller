package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder
public class ActionMatch {
    EButtonAxisMapping trigger;
    @Builder.Default
    Set<EButtonAxisMapping> modifiers = new HashSet<>();
    boolean longPress;
}
