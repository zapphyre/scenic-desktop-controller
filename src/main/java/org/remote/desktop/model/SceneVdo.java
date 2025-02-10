package org.remote.desktop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneVdo {

    Long id;

    String name;
    String windowName;
    SceneVdo inherits;

    List<ActionVdo> actions = new LinkedList<>();
}
