package org.remote.desktop.model;

import lombok.*;
import org.asmus.model.EButtonAxisMapping;

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
public class ActionVto {

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
    private Set<EButtonAxisMapping> modifiers = new HashSet<>();
    @Builder.Default
    private List<XdoActionVto> actions = new LinkedList<>();

    private SceneVto scene;
}
