package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mark.CacheEvictAll;
import org.remote.desktop.model.dto.rest.TrieResult;
import org.remote.desktop.model.vto.LanguageVto;
import org.remote.desktop.service.impl.LanguageService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/languages")
@RequiredArgsConstructor
public class LanguageCtrl {

    private final LanguageService languageService;

    @PostMapping
    public Long saveLanguage(@RequestBody LanguageVto language) {
        return languageService.create(language);
    }

    @CacheEvictAll
    @PostMapping(
            value = "init/{langId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Mono<Integer> handleUpload(@PathVariable("langId") Long languageId,
                                      @RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart ->
                DataBufferUtils.join(filePart.content()) // joins all data buffers
                        .map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            DataBuffer read = dataBuffer.read(bytes);
                            DataBufferUtils.release(read); // always release buffers

                            return bytes;
                        })
                        .map(languageService.addVocabulary(languageId))
        );
    }

    @GetMapping("{langId}/suggest/{term}")
    public TrieResult suggestFor(@PathVariable("langId") Long languageId, @PathVariable("term") String term) {
        return languageService.suggestFor(languageId, term);
    }

    @GetMapping("all")
    public List<LanguageVto> getLanguages() {
        return languageService.getAll();
    }

    @PutMapping
    public void update(@RequestBody LanguageVto language) {
        languageService.update(language);
    }

    @DeleteMapping("{id}")
    public void deleteLanguage(@PathVariable("id") Long languageId) {
        languageService.delete(languageId);
    }
}
