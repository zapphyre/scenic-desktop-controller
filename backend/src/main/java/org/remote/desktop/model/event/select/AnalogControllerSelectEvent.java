package org.remote.desktop.model.event.select;

import lombok.Value;
import org.remote.desktop.model.EAnalogControl;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.context.ApplicationEvent;

@Value
public class AnalogControllerSelectEvent extends ApplicationEvent {

    EAnalogControl analogControl;
    SceneDto lastScene;

    public AnalogControllerSelectEvent(Object source, EAnalogControl analogControl, SceneDto lastScene) {
        super(source);
        this.analogControl = analogControl;
        this.lastScene = lastScene;
    }
}
