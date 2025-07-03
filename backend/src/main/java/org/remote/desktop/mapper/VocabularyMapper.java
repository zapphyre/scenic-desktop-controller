package org.remote.desktop.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.model.dto.VocabularyAdjustmentDto;

@Mapper(componentModel = "spring")
public interface VocabularyMapper {

    @Mapping(target = "langIdFk", source = "language.id")
    VocabularyAdjustmentDto map(VocabularyAdjustment entity);

    @InheritInverseConfiguration
    VocabularyAdjustment map(VocabularyAdjustmentDto dto);
}
