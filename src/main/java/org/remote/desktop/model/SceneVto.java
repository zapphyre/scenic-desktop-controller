package org.remote.desktop.model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SceneVto {

    @ToString.Include
    @EqualsAndHashCode.Include
    private String name;
    private String windowName;
    private SceneVto inherits;

    @Builder.Default
    private List<ActionVto> actions = new LinkedList<>();
}
