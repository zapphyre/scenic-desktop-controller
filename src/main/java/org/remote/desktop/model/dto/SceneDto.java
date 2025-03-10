package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.GamepadEventContainer;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
//@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name") // Use 'name' as ID
public class SceneDto implements GamepadEventContainer<GamepadEventDto, SceneDto> {

    @ToString.Include
    @EqualsAndHashCode.Include
    String name;
    String windowName;

    SceneDto inherits;

    @Builder.Default
    EAxisEvent leftAxisEvent = EAxisEvent.NOOP;
    @Builder.Default
    EAxisEvent rightAxisEvent = EAxisEvent.NOOP;

    @Builder.Default
    List<GamepadEventDto> gamepadEvents = new LinkedList<>();
}
