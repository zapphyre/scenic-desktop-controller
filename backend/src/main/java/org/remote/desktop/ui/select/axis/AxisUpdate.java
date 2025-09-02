package org.remote.desktop.ui.select.axis;

import lombok.Builder;
import lombok.Value;
import org.remote.desktop.model.EAnalogControl;
import org.remote.desktop.model.dto.SceneDto;

@Value
@Builder
public class AxisUpdate<L, R> {

    EAnalogControl trigger;
    SceneDto sceneDto;
    L left;
    R right;
}
