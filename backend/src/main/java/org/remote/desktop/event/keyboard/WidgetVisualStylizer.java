package org.remote.desktop.event.keyboard;

import lombok.RequiredArgsConstructor;
import org.asmus.model.EQualificationType;
import org.remote.desktop.model.event.keyboard.ButtonEvent;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(CircleButtonsInputWidget.class)
public class WidgetVisualStylizer implements ApplicationListener<ButtonEvent> {

    private final CircleButtonsInputWidget widget;

    @Override
    public void onApplicationEvent(ButtonEvent event) {
        widget.toggleVisual(event.getButton());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return false;
    }
}
