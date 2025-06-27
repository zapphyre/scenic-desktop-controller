package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.service.impl.LanguageService;
import org.springframework.web.bind.annotation.*;

import static org.remote.desktop.service.impl.LanguageService.decrement;
import static org.remote.desktop.service.impl.LanguageService.increment;

@RestController
@RequestMapping("${api.prefix}/adjust/{langId}")
@RequiredArgsConstructor
public class VocabAdjCtrl {

    private final LanguageService languageService;

    @PutMapping("increment/{word}")
    public void adjustVocabUp(@PathVariable("langId") Long langId, @PathVariable("word") String word) {
        languageService.propVocabularyFreq(langId, increment).accept(word);
    }

    @PutMapping("decrement/{word}")
    public void adjustVocabDown(@PathVariable("langId") Long langId, @PathVariable("word") String word) {
        languageService.propVocabularyFreq(langId, decrement).accept(word);
    }
}
