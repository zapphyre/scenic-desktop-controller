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
import java.util.LinkedList;
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

    Function<String, VocabularyAdjustment> propVocabularyFreq(Long languageId,
                                                              Function<VocabularyAdjustment, VocabularyAdjustment> propFun,
                                                              Function<VocabularyAdjustment, VocabularyAdjustment> storeFun) {
        return vocabularyDao.findByLangAndWordOrCreate(languageId)
                .andThen(propFun)
                .andThen(storeFun);
    }

    public Function<String, VocabularyAdjustment> propVocabularyFreq(Long languageId,
                                                                     Function<VocabularyAdjustment, VocabularyAdjustment> propFun) {
        return propVocabularyFreq(languageId, vocabularyDao::save, propFun);
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

    public List<LanguageDto> getAllDto() {
        return languageDao.getAllDto();
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

    public Function<String, VocabularyAdjustmentDto> insertOrPropNonCommiting(Long langId) {
        return insertOrPropUp(langId, unCommitedPropper(langId));
    }

    public Function<String, VocabularyAdjustmentDto> insertOrPropCommiting(Long langId) {
        return insertOrPropUp(langId, commitingPropper(langId));
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

    static Function<String, String> createCharacterMapper(Map<Character, Character> charMap) {
        return input -> input.chars()
                .map(Character::toUpperCase)
                .mapToObj(q -> (char) q)
                .map(charMap::get)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private final List<VocabularyAdjustment> vocabularyAdjustments = new LinkedList<>();

    VocabularyAdjustment addReturning(VocabularyAdjustment vocabularyAdjustment) {
        vocabularyAdjustments.add(vocabularyAdjustment);
        return vocabularyAdjustment;
    }

    Function<String, VocabularyAdjustment> unCommitedPropper(Long langId) {
        return propVocabularyFreq(langId, changeFrequency.apply(increment), this::addReturning);
    }

    Function<String, VocabularyAdjustment> commitingPropper(Long langId) {
        return propVocabularyFreq(langId, changeFrequency.apply(increment), vocabularyDao::save);
    }

    Function<Trie<String>, Trie<String>> insertToTrie(String word) {
        return q -> q.insert(word, word);
    }

    Function<List<ValueFrequency<String>>, VocabularyAdjustmentDto> incrementStoreAndMap(String word,
                                                                                         Function<String, VocabularyAdjustment> freqUpStore) {
        return q -> q.stream()
                .filter(p -> p.getValue().equals(word))
                .map(ValueFrequency::getValue)
                .map(freqUpStore)
                .map(vocabularyMapper::map)
                .findFirst()
                .orElseThrow();
    }

    Function<String, VocabularyAdjustmentDto> insertOrPropUp(Long langId,
                                                             Function<String, VocabularyAdjustment> upStore) {
        return word -> trieService.trieDictForLanguage
                .andThen(insertToTrie(word))
                .andThen(q -> q.getValueFreqSuggestions(wordToTrieEncoder.apply(word)))
                .andThen(incrementStoreAndMap(word, upStore))
                .apply(langId);
    }
}
