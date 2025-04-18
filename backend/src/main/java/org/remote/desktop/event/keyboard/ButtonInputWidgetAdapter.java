package org.remote.desktop.event.keyboard;

import com.arun.trie.base.Trie;
import jakarta.annotation.PostConstruct;
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
public class ButtonInputWidgetAdapter implements ApplicationListener<ButtonEvent> {

    private final CircleButtonsInputWidget widget;
    private final Trie<String> trie;

    @PostConstruct
    void intialize() {
        System.out.println("ButtonInputWidgetAdapter initialized");
    }

    @Override
    public void onApplicationEvent(ButtonEvent event) {
        if (event.getQualification() == EQualificationType.PUSH)
            widget.setActiveAndType(event.getButton().ordinal());
        else
            widget.setActive(event.getButton().ordinal());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
