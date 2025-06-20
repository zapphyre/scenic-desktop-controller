package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;
import org.remote.desktop.model.Behavioral;

import java.util.Set;

@With
@Data()
@ToString
@Builder(toBuilder = true)
@Jacksonized
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class ButtonEventDto implements Behavioral {

    Long id;

    String trigger;
    boolean longPress;
    EMultiplicity multiplicity;

    Set<EButtonAxisMapping> modifiers;

    EventDto event;
}
