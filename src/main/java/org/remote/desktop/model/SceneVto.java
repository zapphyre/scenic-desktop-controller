package org.remote.desktop.model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneVto {

    Long id;

    String name;
    String windowName;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    SceneVto inherits;

    List<ActionVto> actions = new LinkedList<>();
}
