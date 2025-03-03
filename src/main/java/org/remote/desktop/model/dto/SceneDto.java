package org.remote.desktop.model.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EAxisEvent;

import java.util.LinkedList;
import java.util.List;

@Value
@Builder
@Jacksonized
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SceneDto {

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
