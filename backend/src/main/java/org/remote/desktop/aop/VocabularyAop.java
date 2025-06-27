package org.remote.desktop.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.remote.desktop.db.dao.VocabularyDao;
import org.remote.desktop.service.impl.TrieService;
import org.springframework.stereotype.Component;

import static org.remote.desktop.service.impl.LanguageService.wordToTrieEncoder;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VocabularyAop {

    private final VocabularyDao vocabularyDao;
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
}
