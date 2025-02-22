package org.remote.desktop.model;

import lombok.*;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;
import org.remote.desktop.pojo.ReplaceableSet;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GPadEventVto implements Behavioral {

    private Long id;
    private EButtonAxisMapping trigger;
    private boolean longPress;

    private SceneVto nextScene;

    @Builder.Default
    private ReplaceableSet<EButtonAxisMapping> modifiers = new ReplaceableSet<>();
    private List<XdoActionVto> actions = new LinkedList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SceneVto scene;

    private EMultiplicity multiplicity = EMultiplicity.CLICK;

}
