package org.remote.desktop.ui.select.trigger;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.dto.SceneDto;

@With
@Value
@Builder
public class TriggerUpdate {
    String trigger;
    EAxisEaser easer;
    SceneDto sceneDto;
}
