package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EAxisEvent;

import java.util.List;
import java.util.Set;

@With
@Value
@Builder
@Jacksonized
public class SceneVto {
    Long id;
    String name;
    String windowName;

    Set<Long> inheritsIdFk;

    @Builder.Default
    EAxisEvent leftAxisEvent = EAxisEvent.NOOP;
    @Builder.Default
    EAxisEvent rightAxisEvent = EAxisEvent.NOOP;

    List<GamepadEventVto> gamepadEvents;

    List<GamepadEventVto> inheritedGamepadEvents;
}
