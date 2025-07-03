package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.db.repository.LanguageRepository;
import org.remote.desktop.db.repository.VocabularyRepository;
import org.remote.desktop.mapper.VocabularyMapper;
import org.remote.desktop.model.dto.VocabularyAdjustmentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
public class VocabularyDao {

    private final VocabularyRepository vocabularyRepository;
    private final LanguageRepository languageRepository;
    private final VocabularyMapper vocabularyMapper;

    public Function<String, VocabularyAdjustmentDto> findByLangAndWordOrCreate(Long langId) {
        return word -> vocabularyRepository.findByLanguageIdAndWord(langId, word)
                .map(vocabularyMapper::map)
                .orElseGet(() -> createVocabulary(langId).apply(word));
    }

    public List<VocabularyAdjustment> getAllForLanguage(Long langId) {
        return vocabularyRepository.findByLanguageId(langId);
    }

    public List<VocabularyAdjustment> getAllForLanguageRemoving(Long langId) {
        List<VocabularyAdjustment> allForLanguage = getAllForLanguage(langId);

        vocabularyRepository.deleteAll(allForLanguage);

        return allForLanguage;
    }

    public Function<String, VocabularyAdjustmentDto> createVocabulary(Long langId) {
        return word -> Optional.of(VocabularyAdjustment.builder()
                        .language(languageRepository.findById(langId).orElseThrow())
                        .word(word)
                        .build())
                .map(vocabularyRepository::save)
                .map(vocabularyMapper::map)
                .orElseThrow();
    }

    public VocabularyAdjustmentDto save(VocabularyAdjustmentDto vocabularyAdjustment) {
        return Optional.ofNullable(vocabularyAdjustment)
                .map(vocabularyMapper::map)
                .map(vocabularyRepository::save)
                .map(vocabularyMapper::map)
                .orElseThrow();
    }

    public void deleteAllForLanguage(Long langId) {
        vocabularyRepository.deleteAll(vocabularyRepository.findByLanguageId(langId));
    }
}
