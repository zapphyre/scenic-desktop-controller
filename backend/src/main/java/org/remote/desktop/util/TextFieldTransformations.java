package org.remote.desktop.util;

import org.asmus.model.EButtonAxisMapping;

import static org.remote.desktop.util.TextUtil.findPreviousWordStart;

public class TextFieldTransformations {

    public static IdxWordTx deleteOn = (q, p) -> t -> {
        if (p.contains(EButtonAxisMapping.BUMPER_LEFT)) {
            int previousWordStart = findPreviousWordStart(t.getText(), t.getCaretPosition());
            t.deleteText(previousWordStart, t.getText().length());
        } else
            t.deletePreviousChar();
    };
    
    public static IdxWordTx toggleCase = (i, m) -> p -> {
        char targetChar = p.getText().charAt(i - 1);

        char newChar;
        if (Character.isUpperCase(targetChar)) {
            newChar = Character.toLowerCase(targetChar);
        } else if (Character.isLowerCase(targetChar)) {
            newChar = Character.toUpperCase(targetChar);
        } else {
            return; // Not a letter, do nothing
        }

        p.deletePreviousChar();
        p.insertText(i - 1, String.valueOf(newChar));
    };
    
    static IdxWordTx snakeize = (i, m) -> textField -> {
        String text = textField.getText();
        int caretPos = textField.getCaretPosition();

        String[] words = text.split(" ");
        if (words.length == 0 || words[words.length - 1].isEmpty()) return;

        String lastWord = words[words.length - 1];
        int wordStart = text.lastIndexOf(lastWord, caretPos - 1);
        if (wordStart < 0) return;

        String snakeCaseWord = toSnakeCase(lastWord);
        textField.deleteText(wordStart, wordStart + lastWord.length());
        textField.insertText(wordStart, snakeCaseWord);
    };

    // Convert word to snake_case
    private static String toSnakeCase(String word) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (char c : word.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!first) result.append('_');
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
            first = false;
        }
        return result.toString();
    }

    // Transform word before caret to alternate case or back to lowercase
    static IdxWordTx alternateCase = (i, m) -> textField -> {
        String text = textField.getText();
        int caretPos = textField.getCaretPosition();
        if (caretPos == 0) return;

        String[] words = text.substring(0, caretPos).split("\\s+");
        if (words.length == 0 || words[words.length - 1].isEmpty()) return;

        String targetWord = words[words.length - 1];
        int wordStart = text.lastIndexOf(targetWord, caretPos - 1);
        if (wordStart < 0) return;

        boolean isAlternate = targetWord.length() >= 2 &&
                Character.isLowerCase(targetWord.charAt(0)) &&
                Character.isUpperCase(targetWord.charAt(1));

        String transformed = isAlternate ? targetWord.toLowerCase() : toAlternateCase(targetWord);
        textField.deleteText(wordStart, wordStart + targetWord.length());
        textField.insertText(wordStart, transformed);
    };

    // Convert word to alternate case
    private static String toAlternateCase(String word) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            result.append(i % 2 == 1 ? Character.toUpperCase(c) : Character.toLowerCase(c));
        }
        return result.toString();
    }





    // v==========================

    // Transform words before caret to camelCase or undo to space-separated lowercase
    static IdxWordTx camelize = (i, m) -> textField -> {
        String text = textField.getText();
        int caretPos = textField.getCaretPosition();
        if (caretPos == 0) return;

        Range wordRange = getWordRangeBeforeCaret(text, caretPos);
        if (wordRange == null) return;

        String targetText = text.substring(wordRange.start, wordRange.end);
        boolean isCamel = isCamelCase(targetText);

        String transformed = isCamel ? undoCamelCase(targetText) : toCamelCase(targetText.split("\\s+"));
        textField.deleteText(wordRange.start, wordRange.end);
        textField.insertText(wordRange.start, transformed);
    };

    // Helper class for word range
    private static class Range {
        final int start, end;
        Range(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    // Get range of words before caret
    private static Range getWordRangeBeforeCaret(String text, int caretPos) {
        String beforeCaret = text.substring(0, caretPos);
        String[] words = beforeCaret.split("\\s+");
        if (words.length == 0 || words[words.length - 1].isEmpty()) return null;

        String firstWord = words[0];
        int start = text.indexOf(firstWord);
        if (start < 0) return null;

        String lastWord = words[words.length - 1];
        int end = text.lastIndexOf(lastWord, caretPos - 1) + lastWord.length();
        if (end < 0) return null;

        return new Range(start, end);
    }

    private static String toCamelCase(String[] words) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) continue;

            word = word.toLowerCase(); // Ensure word is fully lowercased before applying case rules

            if (i == 0) {
                result.append(word); // First word stays lowercase
            } else {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) result.append(word.substring(1));
            }
        }
        return result.toString();
    }


    // Check if text is camelCase using regex
    private static boolean isCamelCase(String text) {
        return text.matches("^[a-z]+(?:[A-Z][a-z]*)*$");
    }

    // Undo camelCase to space-separated lowercase
    private static String undoCamelCase(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (i > 0 && Character.isUpperCase(c) && Character.isLowerCase(text.charAt(i - 1))) {
                result.append(' ');
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }


}
