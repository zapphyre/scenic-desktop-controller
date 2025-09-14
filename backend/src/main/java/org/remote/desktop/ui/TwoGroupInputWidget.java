package org.remote.desktop.ui;

import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.ui.model.EKeyboardInputButton;

import java.util.List;
import java.util.Set;

public interface TwoGroupInputWidget {

    void toggleVisual(EKeyboardInputButton index);
    int setGroupActive(int index);
    void setActiveAndType(EKeyboardInputButton buttonActivated, Set<EButtonAxisMapping> modifiers);

    void setWordsAvailable(List<String> wordsAvailable);
    void addWordToSentence();
    String getSentenceAndReset();
}
