package org.remote.desktop.ui;

import java.util.List;

public interface TwoGroupInputWidget {

    char setActive(int index);
    int setGroupActive(int index);
    char setActiveAndType(int index);

    void setWordsAvailable(List<String> wordsAvailable);
}
