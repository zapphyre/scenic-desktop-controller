package org.remote.desktop.mapper;

import org.mapstruct.Mapper;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.model.dto.NegativeVocabularyAdjustment;
import org.remote.desktop.model.dto.PositiveVocabularyAdjustment;
import org.remote.desktop.model.dto.VocabularyAdjustmentDto;

@Mapper(componentModel = "spring")
public interface VocabularyMapper {

    default VocabularyAdjustmentDto map(VocabularyAdjustment entity) {
        return entity.getFrequencyAdjustment() > 0 ?
                PositiveVocabularyAdjustment.builder()
                        .word(entity.getWord())
                        .frequencyAdjustment(entity.getFrequencyAdjustment())
                        .build() :
                NegativeVocabularyAdjustment.builder()
                        .word(entity.getWord())
                        .frequencyAdjustment(entity.getFrequencyAdjustment())
                        .build();

    }
}
