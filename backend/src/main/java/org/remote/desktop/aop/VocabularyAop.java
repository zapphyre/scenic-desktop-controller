package org.remote.desktop.aop;

import com.arun.trie.base.Trie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.remote.desktop.db.dao.LanguageDao;
import org.remote.desktop.db.dao.VocabularyDao;
import org.remote.desktop.model.dto.LanguageDto;
import org.remote.desktop.service.impl.TrieService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.service.impl.LanguageService.wordToTrieEncoder;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VocabularyAop {

    private final VocabularyDao vocabularyDao;
    private final LanguageDao languageDao;
    private final TrieService trieService;

    @Pointcut("execution(* org.remote.desktop.controller.impl.LanguageCtrl.handleUpload(Long, ..)) && args(languageId,..)")
    void newLanguageBaseLoad(Long languageId) {
    }

    @Pointcut(value = "execution(* org.remote.desktop.controller.impl.VocabAdjCtrl.adjustVocabUp(Long, String)) && args(languageId, word)",
            argNames = "languageId,word")
    void adjustVocabUp(Long languageId, String word) {
    }

    @Pointcut(value = "execution(* org.remote.desktop.controller.impl.VocabAdjCtrl.adjustVocabDown(Long, String)) && args(languageId, word)",
            argNames = "languageId,word")
    void adjustVocabDown(Long languageId, String word) {
    }

    @Pointcut(value = "execution(* org.remote.desktop.controller.impl.VocabAdjCtrl.adjustVocabRemove(Long, String)) && args(languageId, word)",
            argNames = "languageId,word")
    void adjustVocabRemove(Long languageId, String word) {
    }

    @Pointcut(value = "execution(* org.remote.desktop.controller.impl.VocabAdjCtrl.insertOrPropUp(Long, String)) && args(languageId, word)",
            argNames = "languageId,word")
    void adjustVocabInsert(Long languageId, String word) {
    }

    @AfterReturning(value = "newLanguageBaseLoad(languageId)", argNames = "languageId")
    void afterNewLanguageBaseLoad(Long languageId) {
        // b/c adjustments has been materialized in the process
        vocabularyDao.deleteAllForLanguage(languageId);

        log.info("purged vocabulary for language {}", languageId);
    }


    @AfterReturning(value = "adjustVocabUp(languageId, word)", argNames = "languageId,word")
    void propTrieNodeUp(Long languageId, String word) {
        String encoded = wordToTrieEncoder.apply(word);

        trieService.getTrie(languageId)
                .incrementNodeValueFrequency(encoded, word);

        log.info("incremented frequency for word '{}' encoded as trie '{}' ", word, encoded);
    }

    @AfterReturning(value = "adjustVocabDown(languageId, word)", argNames = "languageId,word")
    void propTrieNodeDown(Long languageId, String word) {
        String encoded = wordToTrieEncoder.apply(word);

        trieService.getTrie(languageId)
                .decrementNodeValueFrequency(encoded, word);

        log.info("decremented frequency for word '{}' encoded as trie '{}' ", word, encoded);
    }

    @AfterReturning(value = "adjustVocabRemove(languageId, word)", argNames = "languageId,word")
    void propTrieNodeRemove(Long languageId, String word) {
        String encoded = wordToTrieEncoder.apply(word);

        updateTrieSize(trieService.getTrie(languageId), t -> t.deleteKey(encoded))
                .apply(languageId);

        log.info("removed key for word '{}' encoded as trie '{}' ", word, encoded);
    }

    // before, b/c i need to refer to the real size of valueFreqSuggestions in the service
    @Before(value = "adjustVocabInsert(languageId, word)", argNames = "languageId,word")
    void insertOrPropUp(Long languageId, String word) {

        updateTrieSize(trieService.getTrie(languageId), t -> t.insert(word, word))
                .apply(languageId);
    }

    Function<Long, LanguageDto> languageById = languageDao::getLanguageById;
    Function<Long, LanguageDto> updateTrieSize(Trie<String> trie, Consumer<Trie<String>> trieAction) {
        trieAction.accept(trie);
        return languageById
                .andThen(q -> q.withSize(trie.size()))
                .andThen(languageDao::save);
    }
}
