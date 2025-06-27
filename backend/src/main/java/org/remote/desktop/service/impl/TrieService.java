package org.remote.desktop.service.impl;

import com.arun.trie.base.Trie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.LanguageDao;
import org.remote.desktop.db.dao.VocabularyDao;
import org.remote.desktop.model.dto.LanguageDto;
import org.remote.desktop.prediction.G4Trie;
import org.remote.desktop.util.FluxUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.function.Predicate.not;
import static org.remote.desktop.service.impl.LanguageService.bytesToLines;
import static org.remote.desktop.util.KeyboardButtonFunctionDefinition.trieDict;

@Service
@RequiredArgsConstructor
public class TrieService {

    private final LanguageDao languageDao;
    private final VocabAdjustmentsService vocabAdjustmentsService;

    private final Map<Long, Trie<String>> langIdTrieMap = new ConcurrentHashMap<>();

    Function<Long, Trie<String>> trieDictForLanguage = this::getTrie;
    Function<Long, LanguageDto> languageById() {
        return languageDao::getLanguageById;
    }

    @Transactional(rollbackOn = Exception.class)
    public Trie<String> getTrie(Long languageId) {
        return langIdTrieMap.computeIfAbsent(languageId, languageById()
                        .andThen(LanguageDto::getTrieDump)
                        .andThen(bytesToLines)
                        .andThen(vocabAdjustmentsService.concatMaterializedAdjustments(languageId))
                        .andThen(languageDao.updateTrieDumpOn(languageId))
                        .andThen(insert(new G4Trie(trieDict)))
        );
    }

    public Function<byte[], Trie<String>> loadVocabularyToTrie(Long languageId) {
        return trieDictForLanguage
                .andThen(this::insert)
                .apply(languageId);
    }

    Function<byte[], Trie<String>> insert(Trie<String> trie) {
        return bytes -> new String(bytes, StandardCharsets.UTF_8).lines()
                .map(String::trim)
                .filter(not(String::isEmpty))
                .reduce(trie, (p, q) -> p.insert(q, q), FluxUtil.laterMerger());
    }
}