package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import java.util.List;

@With
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class EventDto {

    @EqualsAndHashCode.Include
    Long id;

    GestureEventDto gestureEvent;
    ButtonEventDto buttonEvent;

    SceneDto scene;
    @ToString.Exclude
    SceneDto nextScene;

    @ToString.Exclude
    List<XdoActionDto> actions;

}
