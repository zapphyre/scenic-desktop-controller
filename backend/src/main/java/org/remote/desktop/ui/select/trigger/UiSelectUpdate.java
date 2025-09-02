package org.remote.desktop.ui.select.trigger;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.remote.desktop.model.dto.SceneDto;

@With
@Value
@Builder
public class UiSelectUpdate<T> {
    String trigger;
    T analogControl;
    SceneDto sceneDto;
}
