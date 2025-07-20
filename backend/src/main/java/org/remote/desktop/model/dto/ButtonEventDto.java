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
@Data
@Builder(toBuilder = true)
@Jacksonized
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class ButtonEventDto implements Behavioral {

    Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    String trigger;
    boolean longPress;
    @EqualsAndHashCode.Include
    EMultiplicity multiplicity;

    @ToString.Include
    @EqualsAndHashCode.Include
    Set<EButtonAxisMapping> modifiers;

    EventDto event;
}
