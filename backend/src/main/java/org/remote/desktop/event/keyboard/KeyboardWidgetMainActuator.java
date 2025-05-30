package org.remote.desktop.event.keyboard;

import lombok.RequiredArgsConstructor;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.event.keyboard.PredictionControlEvent;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeyboardWidgetMainActuator implements ApplicationListener<PredictionControlEvent> {

    private final CircleButtonsInputWidget widget;
    private final List<String> regularButtons = List.of("A", "X", "Y", "B");

    public void longClick(String trigger) {
        widget.activatePrecisionMode(EActionButton.valueOf(trigger));
    }

    @Override
    public void onApplicationEvent(PredictionControlEvent event) {
        if (event.getType().equalsIgnoreCase("DOWN"))
            widget.selectBottomRow();

        if (event.getType().equalsIgnoreCase("UP"))
            widget.selectTopRow();

        if (event.getType().equalsIgnoreCase("LEFT"))
            if (event.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT))
                widget.moveCursorWordLeft();
            else
                widget.moveCursorLeft();

        if (event.getType().equalsIgnoreCase("RIGHT"))
            if (event.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT))
                widget.moveCursorWordRight();
            else
                widget.moveCursorRight();

        if (event.getType().equals("BUMPER_LEFT"))
            widget.resetStateClean();

        if (event.getType().equals("BUMPER_RIGHT"))
            widget.addWordToSentence();

        if (event.getType().equals("RIGHTTRIGGER_ENGAGE"))
            widget.nextPredictionsFrame();

        if (event.getType().equals("LEFTTRIGGER_ENGAGE"))
            copyToClipboard(widget.getSentenceAndReset());

        if (regularButtons.contains(event.getType()))
            widget.setActiveAndType(EActionButton.valueOf(event.getType()), event.getModifiers());

    }

    void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    @Override
    public boolean supportsAsyncExecution() {
        return false;
    }
}
