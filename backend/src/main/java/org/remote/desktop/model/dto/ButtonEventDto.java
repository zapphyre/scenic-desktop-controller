package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;
import org.remote.desktop.model.Behavioral;

import java.util.List;
import java.util.Set;

@Value
@ToString
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class ButtonEventDto implements Behavioral {

    Long id;

    String trigger;
    boolean longPress;
    EMultiplicity multiplicity;
    Set<EButtonAxisMapping> modifiers;

}
