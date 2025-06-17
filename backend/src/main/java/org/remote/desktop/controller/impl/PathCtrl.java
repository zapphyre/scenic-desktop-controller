package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mark.CacheEvictAll;
import org.remote.desktop.model.dto.rest.EStick;
import org.remote.desktop.model.dto.rest.NewGestureRequestDto;
import org.remote.desktop.model.vto.GesturePathVto;
import org.remote.desktop.service.impl.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${api.prefix}/path")
@RequiredArgsConstructor
public class PathCtrl {

    private final PathService pathService;

    @PutMapping("{id}/{newPath}")
    public void updatePathOn(@PathVariable("id") Long id, @PathVariable("newPath") String newPath) {
        pathService.updatePathOn(id, newPath);
    }

    @CacheEvictAll
    @GetMapping("new-path/{id}/{stick}")
    public Mono<ResponseEntity<GesturePathVto>> addPath(@PathVariable("id") Long id, @PathVariable("stick") EStick stick) {
        return pathService.catchGesture(NewGestureRequestDto.builder()
                .id(id)
                .stick(stick)
                .build());
    }

    @DeleteMapping("{id}")
    public void deletePathOn(@PathVariable("id") Long id) {
        pathService.deletePath(id);
    }
}
