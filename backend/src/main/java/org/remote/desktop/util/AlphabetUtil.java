package org.remote.desktop.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class AlphabetUtil {

    public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    List<String> alphabetList = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z");

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

    public static List<List<String>> defaultAlphabetGroups(int groupSize) {
        return splitIntoGroups(alphabetList).apply(groupSize);
    }

    public static <T> Function<Integer, List<List<T>>> splitIntoGroups(List<T> elements) {
        return groupSize -> IntStream.range(0, (elements.size() + groupSize - 1) / groupSize)
                .mapToObj(i -> elements.subList(i * groupSize, Math.min((i + 1) * groupSize, elements.size())))
                .collect(Collectors.toList());
    }
}
