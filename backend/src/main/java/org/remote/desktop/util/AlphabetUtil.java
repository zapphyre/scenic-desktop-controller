package org.remote.desktop.util;

import lombok.experimental.UtilityClass;

import java.util.function.Function;
import java.util.stream.IntStream;

@UtilityClass
public class AlphabetUtil {

    public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String[] getLetterGroups(int numberOfGroups) {
        if (numberOfGroups <= 0 || numberOfGroups > 26) {
            throw new IllegalArgumentException("Number of groups must be between 1 and 26");
        }

        int lettersPerGroup = alphabet.length() / numberOfGroups;
        int remainder = alphabet.length() % numberOfGroups;

        String[] groups = new String[numberOfGroups];
        int letterIndex = 0;

        for (int i = 0; i < numberOfGroups; i++) {
            int groupSize = lettersPerGroup + (i < remainder ? 1 : 0);
            StringBuilder group = new StringBuilder();
            for (int j = 0; j < groupSize && letterIndex < alphabet.length(); j++) {
                group.append(alphabet.charAt(letterIndex++));
            }
            groups[i] = group.toString();
        }

        return groups;
    }

    public static String[] defaultAlphabetGroups(int groupSize) {
        return splitIntoGroups(groupSize).apply(alphabet);
    }

    public static Function<String, String[]> splitIntoGroups(int size) {
        return alphabet -> {
            if (size < 1) {
                throw new IllegalArgumentException("Group size must be at least 1");
            }

            return IntStream.range(0, (alphabet.length() + size - 1) / size)
                    .mapToObj(i -> {
                        int start = i * size;
                        int end = Math.min(start + size, alphabet.length());
                        return alphabet.substring(start, end);
                    })
                    .toArray(String[]::new);
        };
    }
}
