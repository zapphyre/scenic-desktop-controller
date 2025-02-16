package org.remote.desktop.model;

import lombok.*;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.component.ReplaceableSet;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class GPadEventVto {

    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    private EButtonAxisMapping trigger;
    @ToString.Include
    private boolean longPress;

    private SceneVto nextScene;

    @Builder.Default
    @ToString.Include
    private ReplaceableSet<EButtonAxisMapping> modifiers = new ReplaceableSet<>();
    @Builder.Default
    private Set<XdoActionVto> actions = new HashSet<>();

    private SceneVto scene;
}
