package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.ButtonEvent;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.remote.desktop.ui.InputWidgetBase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(CircleButtonsInputWidget.class)
public class ButtonInputWidgetAdapter implements ApplicationListener<ButtonEvent> {

    private final CircleButtonsInputWidget widget;

    @PostConstruct
    void intialize() {
        System.out.println("ButtonInputWidgetAdapter initialized");
    }

    @Override
    public void onApplicationEvent(ButtonEvent event) {
        System.out.println("ButtonInputWidgetAdapter onApplicationEvent: " + event.getButton());
        widget.setElementActive(event.getButton().ordinal());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
