package org.remote.desktop.util;

import lombok.experimental.UtilityClass;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

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

    public static String extractMethodName(Serializable lambda) {
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);

            SerializedLambda serialized = (SerializedLambda) writeReplace.invoke(lambda);
            return serialized.getImplMethodName();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract method name", e);
        }
    }
}
