package org.remote.desktop.model.dto;

import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Value
@SuperBuilder
public class PositiveVocabularyAdjustment extends VocabularyAdjustmentDto {

    public List<String> adjust(List<String> lines) {
        return Stream.concat(lines.stream(),
                        IntStream.range(0, frequencyAdjustment).mapToObj(_ -> word))
                .collect(Collectors.toList());
    }
}
