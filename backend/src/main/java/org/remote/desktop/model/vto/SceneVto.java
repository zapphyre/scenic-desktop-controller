package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EAxisEaser;
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
    EAxisEaser leftAxisEaser = EAxisEaser.NONE;

    @Builder.Default
    EAxisEvent rightAxisEvent = EAxisEvent.NOOP;
    @Builder.Default
    EAxisEaser rightAxisEaser = EAxisEaser.NONE;

    @Builder.Default
    EAxisEaser leftTriggerEaser = EAxisEaser.NONE;
    @Builder.Default
    EAxisEaser rightTriggerEaser = EAxisEaser.NONE;

    List<EventVto> events;

    List<EventVto> inheritedGamepadEvents;

    String mode;
}
