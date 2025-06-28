package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.db.repository.LanguageRepository;
import org.remote.desktop.db.repository.VocabularyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class VocabularyDao {

    private final VocabularyRepository vocabularyRepository;
    private final LanguageRepository languageRepository;

    public Function<String, VocabularyAdjustment> findByLangAndWordOrCreate(Long langId) {
        return word -> vocabularyRepository.findByLanguageIdAndWord(langId, word)
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

    public Function<String, VocabularyAdjustment> createVocabulary(Long langId) {
        return word -> Optional.of(VocabularyAdjustment.builder()
                        .language(languageRepository.findById(langId).orElseThrow())
                        .word(word)
                        .build())
                .map(vocabularyRepository::save)
                .orElseThrow();
    }

    public VocabularyAdjustment save(VocabularyAdjustment vocabularyAdjustment) {
        return vocabularyRepository.save(vocabularyAdjustment);
    }

    public void deleteAllForLanguage(Long langId) {
        vocabularyRepository.deleteAll(vocabularyRepository.findByLanguageId(langId));
    }
}
