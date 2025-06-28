package org.remote.desktop.service.impl;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.LanguageDao;
import org.remote.desktop.db.dao.VocabularyDao;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.mapper.VocabularyMapper;
import org.remote.desktop.model.dto.LanguageDto;
import org.remote.desktop.model.dto.VocabularyAdjustmentDto;
import org.remote.desktop.model.dto.rest.TrieResult;
import org.remote.desktop.model.vto.LanguageVto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.remote.desktop.service.impl.VocabAdjustmentsService.concatTextDocs;
import static org.remote.desktop.util.KeyboardButtonFunctionDefinition.trieDict;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageDao languageDao;
    private final VocabularyDao vocabularyDao;

    private final TrieService trieService;
    private final VocabAdjustmentsService vocabAdjustmentsService;

    private final VocabularyMapper vocabularyMapper;

    public static final Function<String, String> wordToTrieEncoder = createCharacterMapper(trieDict);

    public Function<String, VocabularyAdjustment> propVocabularyFreq(Long languageId,
                                                                     Function<VocabularyAdjustment, VocabularyAdjustment> propFun) {
        return vocabularyDao.findByLangAndWordOrCreate(languageId)
                .andThen(propFun)
                .andThen(vocabularyDao::save);
    }

    static Function<Integer, Integer> nonNullInt = q -> Optional.ofNullable(q).orElse(0);

    public static Function<Integer, Integer> increment = q -> q + 1;
    public static Function<Integer, Integer> decrement = q -> q - 1;
    public static Function<Integer, Integer> remove = q -> Integer.MIN_VALUE;

    public static Function<Function<Integer, Integer>, Function<VocabularyAdjustment, VocabularyAdjustment>> changeFrequency =
            q -> p -> nonNullInt.andThen(q)
                    .andThen(p::withFrequencyAdjustment)
                    .apply(p.getFrequencyAdjustment());

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
        return input -> input.chars()
                .map(Character::toUpperCase)
                .mapToObj(q -> (char) q)
                .map(charMap::get)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public Function<String, VocabularyAdjustmentDto> insertOrPropUp(Long langId) {
        return word -> trieService.getTrie(langId)
                .getValueFreqSuggestions(wordToTrieEncoder.apply(word)).stream()
                .filter(q -> q.getValue().equals(word))
                .map(ValueFrequency::getValue)
                .map(propVocabularyFreq(langId, changeFrequency.apply(increment)))
                .map(vocabularyMapper::map)
                .findFirst()
                .orElseThrow();
    }
}
