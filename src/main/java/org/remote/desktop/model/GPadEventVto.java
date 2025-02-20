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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class GPadEventVto implements Behavioral {

    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    private EButtonAxisMapping trigger;
    @ToString.Include
    private boolean longPress;

    private SceneVto nextScene;

//    @Builder.Default
    @ToString.Include
    private ReplaceableSet<EButtonAxisMapping> modifiers;
    @Builder.Default
    private Set<XdoActionVto> actions = new HashSet<>();

    private SceneVto scene;

    private EMultiplicity multiplicity = EMultiplicity.CLICK;

}
