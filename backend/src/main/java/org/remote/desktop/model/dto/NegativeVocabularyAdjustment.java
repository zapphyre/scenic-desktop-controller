package org.remote.desktop.model.dto;

import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value
@SuperBuilder
public class NegativeVocabularyAdjustment extends VocabularyAdjustmentDto {

    public List<String> adjust(List<String> lines) {
        int cycles = Math.abs(frequencyAdjustment);
        AtomicInteger i = new AtomicInteger(cycles);

        return lines.stream()
                .filter(q -> !q.equals(word) || i.decrementAndGet() < 0)
                .toList();
    }
}
