package org.remote.desktop.model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SceneVto {

    private Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String name;
    private String windowName;
    private SceneVto inherits;

    private List<ActionVto> actions = new LinkedList<>();
}
