package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.asmus.model.EMultiplicity;

import java.util.List;

@Value
@Builder
@Jacksonized
public class ButtonEventVto {

    Long id;

    String trigger;
    boolean longPress;
    EMultiplicity multiplicity;
    List<String> modifiers;

}
