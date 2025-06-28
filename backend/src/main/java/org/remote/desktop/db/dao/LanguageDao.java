package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Language;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.db.repository.LanguageRepository;
import org.remote.desktop.mapper.LanguageMapper;
import org.remote.desktop.model.dto.LanguageDto;
import org.remote.desktop.model.vto.LanguageVto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@Transactional
@RequiredArgsConstructor
public class LanguageDao {

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    public List<LanguageVto> readAll() {
        return languageRepository.findAll().stream()
                .map(languageMapper::map)
                .toList();
    }

    public Long create(LanguageVto language) {
        return Optional.ofNullable(language)
                .map(languageMapper::map)
                .map(languageRepository::save)
                .map(Language::getId)
                .orElseThrow();
    }

    public void update(LanguageVto language) {
        languageRepository.findById(language.getId())
                .ifPresent(languageMapper.update(language));
    }

    public void delete(Long languageId) {
        languageRepository.deleteById(languageId);
    }

    public Function<byte[], byte[]> updateTrieDumpOn(Long langId) {
        return newVocabulary -> languageRepository.findById(langId)
                .map(q -> q.withTrieDump(newVocabulary))
                .map(languageRepository::save)
                .map(Language::getTrieDump)
                .orElseThrow();
    }

    public Function<byte[], byte[]> setVocabulary(Long langId) {
        return bytes -> languageRepository.findById(langId)
                .map(language -> language.withTrieDump(bytes))
                .map(languageRepository::save)
                .map(Language::getTrieDump)
                .orElseThrow();
    }

    public Function<Integer, Integer> setVocabularySize(Long langId) {
        return size -> languageRepository.findById(langId)
                .map(q -> q.withSize(size))
                .map(languageRepository::save)
                .map(Language::getSize)
                .orElseThrow();
    }

    public LanguageDto getLanguageById(Long langId) {
        return languageRepository.findById(langId)
                .map(languageMapper::mapDto)
                .orElseThrow();
    }

    public LanguageDto save(LanguageDto dto) {
        return Optional.ofNullable(dto)
                .map(languageMapper::map)
                .map(languageRepository::save)
                .map(languageMapper::mapDto)
                .orElseThrow();
    }
}