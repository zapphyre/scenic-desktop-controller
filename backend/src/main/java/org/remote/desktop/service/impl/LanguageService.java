package org.remote.desktop.service.impl;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.LanguageDao;
import org.remote.desktop.db.dao.VocabularyDao;
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
import static org.remote.desktop.util.FluxUtil.asFun;
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
    private final List<VocabularyAdjustmentDto> vocabularyAdjustments = new LinkedList<>();


    Function<String, VocabularyAdjustmentDto> propVocabularyFreq(Long languageId,
                                                              Function<Long, Function<String, VocabularyAdjustmentDto>> creatorFun,
                                                              Function<VocabularyAdjustmentDto, VocabularyAdjustmentDto> propFun,
                                                              Function<VocabularyAdjustmentDto, VocabularyAdjustmentDto> storeFun) {
        return creatorFun.apply(languageId)
                .andThen(propFun)
                .andThen(storeFun);
    }

    public Function<String, VocabularyAdjustmentDto> propVocabularyFreq(Long languageId,
                                                                     Function<VocabularyAdjustmentDto, VocabularyAdjustmentDto> propFun) {
        return propVocabularyFreq(languageId, vocabularyDao::findByLangAndWordOrCreate, propFun, vocabularyDao::save);
    }


    static Function<Integer, Integer> nonNullInt = q -> Optional.ofNullable(q).orElse(0);

    public static Function<Integer, Integer> increment = q -> q + 1;
    public static Function<Integer, Integer> decrement = q -> q - 1;
    public static Function<Integer, Integer> remove = q -> Integer.MIN_VALUE;

    public static Function<Function<Integer, Integer>, Function<VocabularyAdjustmentDto, VocabularyAdjustmentDto>> changeFrequency =
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

    Function<String, VocabularyAdjustmentDto> unCommitedPropper(Long langId) {
        return propVocabularyFreq(langId, idVocabBuilder, changeFrequency.apply(increment), asFun(vocabularyAdjustments::add));
    }

    Function<Long, Function<String, VocabularyAdjustmentDto>> idVocabBuilder = q -> p ->
            VocabularyAdjustmentDto.builder()
                    .langIdFk(q)
                    .word(p)
                    .build();

    Function<String, VocabularyAdjustmentDto> commitingPropper(Long langId) {
        return propVocabularyFreq(langId, vocabularyDao::findByLangAndWordOrCreate, changeFrequency.apply(increment), vocabularyDao::save);
    }

    Function<Trie<String>, Trie<String>> insertToTrie(String word) {
        return q -> q.insert(word, word);
    }

    Function<List<ValueFrequency<String>>, VocabularyAdjustmentDto> incrementStoreAndMap(String word,
                                                                                         Function<String, VocabularyAdjustmentDto> freqUpStore) {
        return q -> q.stream()
                .filter(p -> p.getValue().equals(word))
                .map(ValueFrequency::getValue)
                .map(freqUpStore)
                .findFirst()
                .orElseThrow();
    }

    public Function<Long, Trie<String>> trieDictForLanguage() {
        return trieService::getTrie;
    }

    Function<String, VocabularyAdjustmentDto> insertOrPropUp(Long langId,
                                                             Function<String, VocabularyAdjustmentDto> upStore) {
        return word -> trieDictForLanguage()
                .andThen(insertToTrie(word))
                .andThen(q -> q.getValueFreqSuggestions(wordToTrieEncoder.apply(word)))
                .andThen(incrementStoreAndMap(word, upStore))
                .apply(langId);
    }
}
