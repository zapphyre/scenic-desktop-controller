package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

@With
@Value
@Builder
@ToString
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class VocabularyAdjustmentDto {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Long id;

    String word;
    int frequencyAdjustment;

    Long langIdFk;
}
