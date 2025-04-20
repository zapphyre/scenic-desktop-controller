package org.remote.desktop.ui;

import org.remote.desktop.ui.model.EActionButton;

import java.util.List;

public interface TwoGroupInputWidget {

    void toggleVisual(EActionButton index);
    int setGroupActive(int index);
    void setActiveAndType(EActionButton index);

    void setWordsAvailable(List<String> wordsAvailable);
    void addWordToSentence();
    String getSentenceAndReset();
}
