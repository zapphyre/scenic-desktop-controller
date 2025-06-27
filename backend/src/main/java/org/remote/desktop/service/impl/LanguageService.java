package org.remote.desktop.service.impl;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.LanguageDao;
import org.remote.desktop.db.dao.VocabularyDao;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.model.dto.LanguageDto;
import org.remote.desktop.model.dto.rest.TrieResult;
import org.remote.desktop.model.vto.LanguageVto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.service.impl.VocabAdjustmentsService.concatTextDocs;
import static org.remote.desktop.util.KeyboardButtonFunctionDefinition.trieDict;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageDao languageDao;
    private final VocabularyDao vocabularyDao;
    private final VocabAdjustmentsService vocabAdjustmentsService;

    private final TrieService trieService;
    public static final Function<String, String> wordToTrieEncoder = createCharacterMapper(trieDict);

    public Consumer<String> propVocabularyFreq(Long languageId,
                                               Function<VocabularyAdjustment, VocabularyAdjustment> propFun) {
        return word -> vocabularyDao.findByLangAndWord(languageId)
                .andThen(propFun)
                .andThen(vocabularyDao::save)
                .apply(word);
    }

    static Function<Integer, Integer> nonNullInt = q -> Optional.ofNullable(q).orElse(0);

    public static Function<VocabularyAdjustment, VocabularyAdjustment> increment = q ->
            nonNullInt.andThen(a -> ++a)
                    .andThen(q::withFrequencyAdjustment)
                    .apply(q.getFrequencyAdjustment());

    public static Function<VocabularyAdjustment, VocabularyAdjustment> decrement = q ->
            nonNullInt.andThen(a -> --a)
                    .andThen(q::withFrequencyAdjustment)
                    .apply(q.getFrequencyAdjustment());

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
                .map(concatTextDocs(newVocab))
                .map(bytesToLines)
                .map(vocabAdjustmentsService.concatMaterializedAdjustments(langId))
                .map(languageDao.setVocabulary(langId))
                .map(trieService.loadVocabularyToTrie(langId))
                .map(Trie::size)
                .map(languageDao.setVocabularySize(langId))
                .orElseThrow();
    }

    public static Function<byte[], List<String>> bytesToLines = q ->
            new String(q, StandardCharsets.UTF_8).lines().toList();

    public TrieResult suggestFor(Long langId, String term) {
        String encoded = wordToTrieEncoder.apply(term);
        List<ValueFrequency<String>> results = trieService.getTrie(langId)
                .getValueFreqSuggestions(encoded).stream()
                .sorted()
                .toList();

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
}
