package org.remote.desktop.util;

import org.remote.desktop.model.LF;

@FunctionalInterface
public interface LetterIdxGetter {

    LF getLetterIdx(int letterIdx);
}
