package org.remote.desktop.event.keyboard;

import lombok.RequiredArgsConstructor;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.event.keyboard.KeyboardWidgetControlEvent;
import org.remote.desktop.model.event.keyboard.LongHoldEvent;
import org.remote.desktop.model.event.keyboard.PredictionControlEvent;
import org.remote.desktop.service.impl.SceneManager;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.remote.desktop.ui.model.EKeyboardInputButton;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.remote.desktop.mode.modul.KeyboardModule.copyToClipboard;

//@Component
@RequiredArgsConstructor
public class KeyboardWidgetMainActuator implements ApplicationListener<PredictionControlEvent> {

    private final CircleButtonsInputWidget widget;
    private final SceneManager xdoSceneService;
    private final List<String> regularButtons = List.of("A", "X", "Y", "B");

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

//        if (event.getType().equals("LEFTTRIGGER_ENGAGE"))
        if (event.getType().equals("RIGHTTRIGGER_EDGING_NEGATIVE"))
            widget.prevPredictionsFrame();

        if (event.getType().equals("LEFTTRIGGER_ENGAGE")) {
            copyToClipboard(widget.getSentenceAndReset());
            xdoSceneService.tryGetCurrentName();
        }

        if (regularButtons.contains(event.getType()))
            widget.setActiveAndType(EKeyboardInputButton.valueOf(event.getType()), event.getModifiers());

    }

    @Override
    public boolean supportsAsyncExecution() {
        return false;
    }

    @Component
    class KeyboardLongActuator implements ApplicationListener<LongHoldEvent> {
        @Override
        public void onApplicationEvent(LongHoldEvent event) {
            widget.activatePrecisionMode(event.getButton());
        }
    }

    @Component
    class PasteActuator implements ApplicationListener<KeyboardWidgetControlEvent> {
        @Override
        public void onApplicationEvent(KeyboardWidgetControlEvent event) {
            if (event.isOn())
                widget.render();
//            else
//                XdoMouseAct.paste();
        }
    }
}
