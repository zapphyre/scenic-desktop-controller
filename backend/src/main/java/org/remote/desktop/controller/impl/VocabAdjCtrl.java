package org.remote.desktop.controller.impl;

import com.arun.trie.base.ValueFrequency;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.dto.VocabularyAdjustmentDto;
import org.remote.desktop.service.impl.LanguageService;
import org.springframework.web.bind.annotation.*;

import static org.remote.desktop.service.impl.LanguageService.*;

@RestController
@RequestMapping("${api.prefix}/adjust/{langId}/{word}")
@RequiredArgsConstructor
public class VocabAdjCtrl {

    private final LanguageService languageService;

    @PostMapping
    public VocabularyAdjustmentDto insertOrPropUp(@PathVariable("langId") Long langId, @PathVariable("word") String word) {
        return languageService.insertOrPropUp(langId).apply(word);
    }

    @PutMapping("increment")
    public void adjustVocabUp(@PathVariable("langId") Long langId, @PathVariable("word") String word) {
        languageService.propVocabularyFreq(langId, changeFrequency.apply(increment)).apply(word);
    }

    @PutMapping("decrement")
    public void adjustVocabDown(@PathVariable("langId") Long langId, @PathVariable("word") String word) {
        languageService.propVocabularyFreq(langId, changeFrequency.apply(decrement)).apply(word);
    }

    @DeleteMapping("remove")
    public void adjustVocabRemove(@PathVariable("langId") Long langId, @PathVariable("word") String word) {
        languageService.propVocabularyFreq(langId, changeFrequency.apply(remove)).apply(word);
    }
}
