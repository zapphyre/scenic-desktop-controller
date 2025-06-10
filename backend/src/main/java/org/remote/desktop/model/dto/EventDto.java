package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class EventDto {

    Long id;

    GestureEventDto gestureEvent;
    ButtonEventDto buttonEvent;

    SceneDto scene;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    SceneDto nextScene;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<XdoActionDto> actions;
}
