package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.event.TextInputEvent;
import org.remote.desktop.ui.InputWidget;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeyboardInputActuator implements ApplicationListener<TextInputEvent> {

    private final InputWidget widget;

    @Override
    public void onApplicationEvent(TextInputEvent event) {
        widget.render();

        event.getKeyPart().getKeyStrokes().forEach(e -> {
                    switch (EButtonAxisMapping.getByEnumName(e)) {
                        case Y -> widget.clearText();
                        case X -> widget.close();
                        case B -> widget.render();
                        case BUMPER_RIGHT -> widget.addSelectedLetter();
                        default -> System.out.println("Invalid key event: " + e);
                    }
                }
        );
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
