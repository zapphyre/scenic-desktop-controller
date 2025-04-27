package org.remote.desktop.util;

import org.asmus.model.EButtonAxisMapping;

import java.util.Set;

@FunctionalInterface
public interface IdxWordTx {

    WordGenFun transforIdxWord(int idx, Set<EButtonAxisMapping> modifiers);
}
