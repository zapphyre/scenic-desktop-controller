package org.remote.desktop.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TextUtil {

    public static int findPreviousWordStart(String text, int caretPosition) {
        if (caretPosition <= 0) return 0;
        int pos = caretPosition - 1;
        while (pos > 0 && Character.isWhitespace(text.charAt(pos))) pos--;
        while (pos > 0 && !Character.isWhitespace(text.charAt(pos - 1))) pos--;
        return pos;
    }

    public static int findNextWordStart(String text, int caretPosition) {
        if (caretPosition >= text.length()) return text.length();
        int pos = caretPosition;
        while (pos < text.length() && !Character.isWhitespace(text.charAt(pos))) pos++;
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++;
        return pos;
    }
}
