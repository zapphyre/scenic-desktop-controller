package org.remote.desktop.ui.model;

import org.remote.desktop.util.IdxWordTx;

public interface ButtonInputProcessor {

    void asTrieChar(char c);

    void asLetter(String letter);

    void asFunction(IdxWordTx fx);

    void asDeletingLong(String letter);
}
