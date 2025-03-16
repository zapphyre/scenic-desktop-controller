package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.GamepadEventContainer;

import java.util.LinkedList;
import java.util.List;

@Value
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class SceneDto implements GamepadEventContainer<GamepadEventDto, SceneDto> {

    Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    String name;
    String windowName;

    SceneDto inherits;

    EAxisEvent leftAxisEvent;
    EAxisEvent rightAxisEvent;

    List<GamepadEventDto> gamepadEvents = new LinkedList<>();
}
