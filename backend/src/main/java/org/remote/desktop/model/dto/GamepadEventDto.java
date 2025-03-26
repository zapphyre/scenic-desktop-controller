package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;
import org.remote.desktop.model.Behavioral;
import org.remote.desktop.pojo.ReplaceableSet;

import java.util.List;

@Value
@ToString
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class GamepadEventDto implements Behavioral {

    Long id;
    String trigger;
    boolean longPress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    SceneDto nextScene;

    SceneDto scene;

    ReplaceableSet<EButtonAxisMapping> modifiers = new ReplaceableSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<XdoActionDto> actions;

    EMultiplicity multiplicity = EMultiplicity.CLICK;
}
