package org.remote.desktop.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.GamepadEventContainer;

import java.util.LinkedList;
import java.util.List;

@Value
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
