package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import org.remote.desktop.model.EAxisEvent;

import java.util.List;

@Value
@Builder
public class SceneVto {
    String name;
    String windowName;

    String inheritsNameFk;

    @Builder.Default
    EAxisEvent leftAxisEvent = EAxisEvent.NOOP;
    @Builder.Default
    EAxisEvent rightAxisEvent = EAxisEvent.NOOP;

    List<GamepadEventVto> gamepadEvents;

    List<GamepadEventVto> inheritedGamepadEvents;
}
