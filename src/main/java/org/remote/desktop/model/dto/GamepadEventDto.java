package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;
import org.remote.desktop.model.Behavioral;
import org.remote.desktop.pojo.ReplaceableSet;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
//@Jacksonized //commented out b/c of jackson's cyclic graph serialization/deser. check SceneCtrlTest::canSerializeCyclicGraph
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class GamepadEventDto implements Behavioral {

    Long id;
    EButtonAxisMapping trigger;
    boolean longPress;

    SceneDto nextScene;

    @Builder.Default
    ReplaceableSet<EButtonAxisMapping> modifiers = new ReplaceableSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<XdoActionDto> actions;

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private SceneVto scene;

    EMultiplicity multiplicity = EMultiplicity.CLICK;
}
