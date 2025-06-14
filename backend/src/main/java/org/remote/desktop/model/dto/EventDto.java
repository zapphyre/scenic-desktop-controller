package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class EventDto {

    Long id;

    GestureEventDto gestureEvent;
    ButtonEventDto buttonEvent;

    SceneDto scene;
    @ToString.Exclude
    SceneDto nextScene;

    @ToString.Exclude
    List<XdoActionDto> actions;
}
