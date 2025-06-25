package org.remote.desktop.service.impl;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.LanguageDao;
import org.remote.desktop.db.entity.Vocabulary;
import org.remote.desktop.model.dto.LanguageDto;
import org.remote.desktop.model.dto.rest.TrieResult;
import org.remote.desktop.model.vto.LanguageVto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.remote.desktop.util.KeyboardButtonFunctionDefinition.trieDict;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageDao languageDao;
    private final TrieService trieService;

    private final Sinks.Many<String> suggestions = Sinks.many().multicast().directBestEffort();

    public WordNote addToVocabulary(String language) {
        Function<String, Vocabulary> vocabularyCreator = Optional.ofNullable(language)
                .map(languageDao::getLanguageIdByAbbreviation)
                .map(languageDao::insertWordForLanguageId)
                .orElseThrow();

        return word -> {
            Vocabulary vocabulary = Optional.ofNullable(languageDao.getVocabularyByWord(word))
                    .orElseGet(() -> vocabularyCreator.apply(word));

            vocabulary.setFrequency(vocabulary.getFrequency() + 1);

            languageDao.saveVocabulary(vocabulary);
        };
    }

    public List<LanguageVto> getAll() {
        return languageDao.readAll();
    }

    public Long create(LanguageVto language) {
        return languageDao.create(language);
    }

    public void update(LanguageVto language) {
        languageDao.update(language);
    }

    public void delete(Long languageId) {
        languageDao.delete(languageId);
    }

    public Function<? super byte[], Integer> addVocabulary(Long langId) {
        return newVocab -> Optional.ofNullable(langId)
                .map(languageDao::getLanguageById)
                .map(LanguageDto::getTrieDump)
                .map(concatTextTextDocs(newVocab))
                .map(languageDao.setVocabulary(langId))
                .map(trieService.insertReturning(langId))
                .map(Trie::size)
                .map(languageDao.setVocabularySize(langId))
                .orElseThrow();
    }

    public Flux<String> suggest() {
        return suggestions.asFlux();
    }

    public TrieResult suggestFor(Long langId, String term) {
        String encoded = createCharacterMapper(trieDict).apply(term);

        List<ValueFrequency<String>> results = trieService.getTrie(langId)
                .getValueFreqSuggestions(encoded);

        return TrieResult.builder()
                .encoded(encoded)
                .trie(results)
                .build();
    }

    public static Function<String, String> createCharacterMapper(Map<Character, Character> charMap) {
        return input -> {
            StringBuilder result = new StringBuilder();
            for (char c : input.toCharArray()) {
                Character mappedChar = charMap.get(Character.toUpperCase(c));
                result.append(mappedChar);
            }
            return result.toString();
        };
    }

    public interface WordNote {
        void createOrProp(String word);
    }

    public static Function<byte[], byte[]> concatTextTextDocs(byte[] first) {
        return second -> {

            String str1 = new String(first, StandardCharsets.UTF_8);
            String str2 = new String(second, StandardCharsets.UTF_8);

            // Ensure proper line break between blocks
            String merged = str1.endsWith("\n") || str1.isEmpty()
                    ? str1 + str2
                    : str1 + "\n" + str2;

            return merged.getBytes(StandardCharsets.UTF_8);
        };
    }
}
