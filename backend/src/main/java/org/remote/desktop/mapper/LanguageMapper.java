package org.remote.desktop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.remote.desktop.db.entity.Language;
import org.remote.desktop.model.dto.LanguageDto;
import org.remote.desktop.model.vto.LanguageVto;

import java.util.function.Consumer;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    LanguageVto map(Language language);

    @Mapping(target = "trieDump", ignore = true)
    Language map(LanguageVto languageVto);

    @Mapping(target = "trieDump", ignore = true)
    void update(@MappingTarget Language language, LanguageVto languageVto);

    default Consumer<Language> update(LanguageVto languageVto) {
        return q -> update(q, languageVto);
    }

    LanguageDto mapDto(Language entity);

    Language map(LanguageDto dto );
}
