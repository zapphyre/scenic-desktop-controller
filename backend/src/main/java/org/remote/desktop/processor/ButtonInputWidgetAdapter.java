package org.remote.desktop.processor;

import com.arun.trie.base.Trie;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.ButtonEvent;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(CircleButtonsInputWidget.class)
public class ButtonInputWidgetAdapter implements ApplicationListener<ButtonEvent> {

    private final CircleButtonsInputWidget widget;
    private final Trie<String> trie;
    private final StringBuilder letters = new StringBuilder();

    @PostConstruct
    void intialize() {
        System.out.println("ButtonInputWidgetAdapter initialized");
    }

    @Override
    public void onApplicationEvent(ButtonEvent event) {
        if (event.getButton() == EActionButton.Y)
            letters.setLength(0);

        char key = widget.setElementActive(event.getButton().ordinal());

        letters.append(key);

        List<String> valueSuggestions = trie.getKeySuggestions(letters.toString());
        widget.setWordsAvailable(valueSuggestions);
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
