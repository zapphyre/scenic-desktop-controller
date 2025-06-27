package org.remote.desktop.mapper;

import org.mapstruct.Mapper;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.model.dto.VocabularyAdjustmentDto;

@Mapper(componentModel = "spring")
public interface VocabularyMapper {

    VocabularyAdjustmentDto map(VocabularyAdjustment entity);
}
