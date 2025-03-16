package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.remote.desktop.model.EAxisEvent;

import java.util.List;

@With
@Value
@Builder
public class SceneVto {
    Long id;
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
