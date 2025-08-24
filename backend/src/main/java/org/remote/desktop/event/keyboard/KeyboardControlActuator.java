package org.remote.desktop.event.keyboard;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.actuate.MouseAct;
import org.remote.desktop.model.event.keyboard.KeyboardWidgetControlEvent;
import org.remote.desktop.ui.InputWidgetBase;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeyboardControlActuator implements ApplicationListener<KeyboardWidgetControlEvent> {

    private final InputWidgetBase inputWidgetBase;

    @Override
    public void onApplicationEvent(KeyboardWidgetControlEvent event) {
        if (event.isOn())
            inputWidgetBase.render();
        else
            MouseAct.paste();
    }
}
