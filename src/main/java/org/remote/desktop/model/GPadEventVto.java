package org.remote.desktop.model;

import lombok.*;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EMultiplicity;
import org.remote.desktop.component.ReplaceableSet;

import java.util.HashSet;
import java.util.Set;

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
    private Set<XdoActionVto> actions = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SceneVto scene;

    private EMultiplicity multiplicity = EMultiplicity.CLICK;

}
