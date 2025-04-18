package org.remote.desktop.ui;

import java.util.List;

public interface TwoGroupInputWidget {

    char setActive(int index);
    int setGroupActive(int index);
    void setActiveAndType(int index);

    void setWordsAvailable(List<String> wordsAvailable);
    void addWordToSentence();
    String getSentenceAndReset();
}
