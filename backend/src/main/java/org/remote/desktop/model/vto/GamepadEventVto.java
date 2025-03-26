package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;

import java.util.List;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class GamepadEventVto {

    Long id;
    String trigger;
    boolean longPress;

    Long parentFk;
    Long nextSceneFk;

    Set<EButtonAxisMapping> modifiers;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<XdoActionVto> actions;

    EMultiplicity multiplicity;
}
