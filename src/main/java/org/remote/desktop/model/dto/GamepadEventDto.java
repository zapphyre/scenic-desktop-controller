package org.remote.desktop.model.dto;

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
@Builder
@ToString
@EqualsAndHashCode
public class GamepadEventDto implements Behavioral {

    Long id;
    EButtonAxisMapping trigger;
    boolean longPress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    SceneDto nextScene;

    @Builder.Default
    ReplaceableSet<EButtonAxisMapping> modifiers = new ReplaceableSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<XdoActionDto> actions;

    EMultiplicity multiplicity = EMultiplicity.CLICK;
}
