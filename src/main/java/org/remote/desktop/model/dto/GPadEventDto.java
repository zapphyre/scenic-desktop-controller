package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;
import org.remote.desktop.model.Behavioral;
import org.remote.desktop.pojo.ReplaceableSet;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class GPadEventDto implements Behavioral {

    private Long id;
    private EButtonAxisMapping trigger;
    private boolean longPress;

    private SceneDto nextScene;

    @Builder.Default
    private ReplaceableSet<EButtonAxisMapping> modifiers = new ReplaceableSet<>();

    private List<XdoActionDto> actions = new LinkedList<>();

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private SceneVto scene;

    private EMultiplicity multiplicity = EMultiplicity.CLICK;
}
