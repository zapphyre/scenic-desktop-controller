package org.remote.desktop.ui.select.axis;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.remote.desktop.model.dto.SceneDto;

@With
@Value
@Builder
public class AxisUpdate<L, R> {
    String trigger;
    L left;
    R right;
    SceneDto sceneDto;
}
