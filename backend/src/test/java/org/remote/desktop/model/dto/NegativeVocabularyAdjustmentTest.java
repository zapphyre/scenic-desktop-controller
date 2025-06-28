package org.remote.desktop.model.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NegativeVocabularyAdjustmentTest {

    @Test
    void testRemoveExactNumberOfOccurrences() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(2)
                .build();
        List<String> input = List.of("apple", "banana", "apple", "orange", "apple");
        List<String> expected = List.of("banana", "orange", "apple");
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testRemoveAllOccurrencesWhenFrequencyExceedsCount() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(5)
                .build();
        List<String> input = List.of("apple", "banana", "apple");
        List<String> expected = List.of("banana");
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testNegativeFrequencyAdjustment() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(-2)
                .build();
        List<String> input = List.of("apple", "banana", "apple", "orange", "apple");
        List<String> expected = List.of("banana", "orange", "apple");
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testZeroFrequencyAdjustment() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(0)
                .build();
        List<String> input = List.of("apple", "banana", "apple");
        List<String> expected = List.of("apple", "banana", "apple");
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testEmptyList() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(3)
                .build();
        List<String> input = List.of();
        List<String> expected = List.of();
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testNullList() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(3)
                .build();
        assertThrows(NullPointerException.class, () -> adjuster.adjust(null));
    }

    @Test
    void testWordNotInList() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("grape")
                .frequencyAdjustment(2)
                .build();
        List<String> input = List.of("apple", "banana", "orange");
        List<String> expected = List.of("apple", "banana", "orange");
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testMaxIntFrequencyAdjustment() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(Integer.MAX_VALUE)
                .build();
        List<String> input = List.of("apple", "banana", "apple", "orange");
        List<String> expected = List.of("banana", "orange");
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testSingleElementMatchingWord() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(1)
                .build();
        List<String> input = List.of("apple");
        List<String> expected = List.of();
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testSingleElementNonMatchingWord() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(1)
                .build();
        List<String> input = List.of("banana");
        List<String> expected = List.of("banana");
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testLargeFrequencyWithNoMatches() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("apple")
                .frequencyAdjustment(Integer.MAX_VALUE)
                .build();
        List<String> input = List.of("banana", "orange", "grape");
        List<String> expected = List.of("banana", "orange", "grape");
        assertEquals(expected, adjuster.adjust(input));
    }

    @Test
    void testEmptyWord() {
        NegativeVocabularyAdjustment adjuster = NegativeVocabularyAdjustment.builder()
                .word("")
                .frequencyAdjustment(2)
                .build();
        List<String> input = List.of("", "banana", "", "apple");
        List<String> expected = List.of("banana", "apple");
        assertEquals(expected, adjuster.adjust(input));
    }
}
