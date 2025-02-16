package org.remote.desktop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asmus.model.EButtonAxisMapping;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionVto {

    private Long id;

    private EButtonAxisMapping trigger;
    private boolean longPress;

    private SceneVto nextScene;

    @Builder.Default
    private Set<EButtonAxisMapping> modifiers = new HashSet<>();
    @Builder.Default
    private List<XdoActionVto> actions = new LinkedList<>();

    private SceneVto scene;
}
