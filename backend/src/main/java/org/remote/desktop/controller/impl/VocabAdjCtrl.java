package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.service.impl.LanguageService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.remote.desktop.service.impl.LanguageService.*;

@RestController
@RequestMapping("${api.prefix}/adjust/{langId}")
@RequiredArgsConstructor
public class VocabAdjCtrl {

    private final LanguageService languageService;

    @PutMapping("increment/{word}")
    public void adjustVocabUp(@PathVariable("langId") Long langId, @PathVariable("word") String word) {
        languageService.propVocabularyFreq(langId, changeFrequency.apply(increment)).accept(word);
    }

    @PutMapping("decrement/{word}")
    public void adjustVocabDown(@PathVariable("langId") Long langId, @PathVariable("word") String word) {
        languageService.propVocabularyFreq(langId, changeFrequency.apply(decrement)).accept(word);
    }
}
