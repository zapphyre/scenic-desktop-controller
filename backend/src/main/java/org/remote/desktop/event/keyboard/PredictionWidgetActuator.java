package org.remote.desktop.event.keyboard;

import com.arun.trie.base.Trie;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.event.VirtualInputStateRepository;
import org.remote.desktop.model.event.keyboard.PredictionControlEvent;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static jxdotool.xDoToolUtil.xDo;

@Component
@RequiredArgsConstructor
public class PredictionWidgetActuator implements ApplicationListener<PredictionControlEvent> {

    private final CircleButtonsInputWidget widget;
    private final Trie<String> trie;
    private final VirtualInputStateRepository virtualInputStateRepository;

    @Override
    public void onApplicationEvent(PredictionControlEvent event) {
        System.out.println("got: " + event);

        if (event.getType().equalsIgnoreCase("LEFT"))
            widget.framePreviousPredictedWord();

        if (event.getType().equalsIgnoreCase("RIGHT"))
            widget.frameNextPredictedWord();

        if (event.getType().equals("BUMPER_LEFT"))
            widget.resetStateClean();

        if (event.getType().equals("BUMPER_RIGHT"))
            widget.addWordToSentence();

        if (event.getType().equals("RIGHTTRIGGER_ENGAGE"))
            widget.nextPredictionsFrame();

        if (event.getType().equals("LEFTTRIGGER_ENGAGE")){
            xDo("type", widget.getSentenceAndReset());
            virtualInputStateRepository.setActive(false);
        }
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
