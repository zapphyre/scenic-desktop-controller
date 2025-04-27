package org.remote.desktop.ui;

import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.ui.model.EActionButton;

import java.util.List;
import java.util.Set;

public interface TwoGroupInputWidget {

    void toggleVisual(EActionButton index);
    int setGroupActive(int index);
    void setActiveAndType(EActionButton buttonActivated, Set<EButtonAxisMapping> modifiers);

    void setWordsAvailable(List<String> wordsAvailable);
    void addWordToSentence();
    String getSentenceAndReset();
}
