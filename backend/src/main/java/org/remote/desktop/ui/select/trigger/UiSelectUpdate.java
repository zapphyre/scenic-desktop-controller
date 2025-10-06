package org.remote.desktop.ui.select.trigger;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.model.dto.SceneDto;

@With
@Value
@Builder
public class UiSelectUpdate<T> {

    T element;
    GamepadDevice device;
    SceneDto sceneDto;
}
