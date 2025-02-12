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

    Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    String name;
    String windowName;
    SceneVto inherits;

    List<ActionVto> actions = new LinkedList<>();
}
