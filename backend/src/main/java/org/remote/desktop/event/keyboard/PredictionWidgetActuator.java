package org.remote.desktop.event.keyboard;

import com.arun.trie.base.Trie;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.keyboard.PredictionControlEvent;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PredictionWidgetActuator implements ApplicationListener<PredictionControlEvent> {

    private final CircleButtonsInputWidget widget;
    private final Trie<String> trie;
    private final StringBuilder letters = new StringBuilder();


    @Override
    public void onApplicationEvent(PredictionControlEvent event) {
        System.out.println("got: " + event);

        if (event.getType().equalsIgnoreCase("LEFT"))
            widget.framePreviousPredictedWord();

        if (event.getType().equalsIgnoreCase("RIGHT"))
            widget.frameNextPredictedWord();

        if (event.getType().equals("BUMPER_LEFT"))
            System.out.println("me type: " + widget.getWordAndReset());

        if (event.getType().equals("RIGHTTRIGGER_ENGAGE"))
            widget.nextPredictionsFrame();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
