package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.GamepadEventContainer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.remote.desktop.model.EAxisEaser.CONTINUOUS;

@Value
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class SceneDto implements GamepadEventContainer<EventDto, SceneDto> {

    Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    String name;
    String windowName;

    Set<SceneDto> inheritsFrom;

    EAxisEvent leftAxisEvent;
    EAxisEaser leftAxisEaser;

    EAxisEvent rightAxisEvent = EAxisEvent.SCROLL;
    EAxisEaser rightAxisEaser;

    List<EventDto> events = new LinkedList<>();
}
