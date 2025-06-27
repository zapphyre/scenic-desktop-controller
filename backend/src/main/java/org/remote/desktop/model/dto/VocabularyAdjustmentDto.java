package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.List;
import java.util.stream.IntStream;

@Value
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class VocabularyAdjustmentDto {

    Long id;
    String word;
    int frequencyAdjustment;

    public List<String> adjust(List<String> lines) {
        int cycles = Math.abs(frequencyAdjustment);
        boolean remove = frequencyAdjustment < 0;

        IntStream.range(0, cycles)
                .forEach(_ -> {
                    if (remove)
                        lines.remove(word);
                    else
                        lines.add(word);
                });

        return lines;
    }
}
