package org.remote.desktop.ui;

import java.util.List;

public interface TwoGroupInputWidget {

    int setGroupActive(int index);
    char setElementActive(int index);

    void setWordsAvailable(List<String> wordsAvailable);
}
