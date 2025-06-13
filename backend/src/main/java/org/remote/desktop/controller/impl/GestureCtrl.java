package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.dto.rest.EStick;
import org.remote.desktop.model.dto.rest.NewGestureRequestDto;
import org.remote.desktop.model.vto.GesturePathVto;
import org.remote.desktop.model.vto.GestureVto;
import org.remote.desktop.service.GestureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/gestures")
@RequiredArgsConstructor
public class GestureCtrl {

    private final GestureService gestureService;

    @GetMapping
    public List<GestureVto> getGestures() {
        return gestureService.getAllGestures();
    }

    @PostMapping
    public Long createNewGesture() {
        return gestureService.createNew();
    }

    @PutMapping("update-path/{id}/{newPath}")
    public void updatePathOn(@PathVariable("id") Long id, @PathVariable("newPath") String newPath) {
        gestureService.updatePathOn(id, newPath);
    }

    @DeleteMapping("delete-path/{id}")
    public void deletePathOn(@PathVariable("id") Long id) {
        gestureService.deletePath(id);
    }

    @DeleteMapping("delete-gesture/{id}")
    public void deleteGestureById(@PathVariable("id") Long id) {
        gestureService.deleteGesture(id);
    }

    @PutMapping("update-name/{id}/{name}")
    public void updateName(@PathVariable("id") Long id, @PathVariable("name") String name) {
        gestureService.updateName(id, name);
    }

    @GetMapping("newPath/{id}/{stick}")
    public Mono<ResponseEntity<GesturePathVto>> addPath(@PathVariable("id") Long id, @PathVariable("stick") EStick stick) {
        return gestureService.catchGesture(NewGestureRequestDto.builder()
                .id(id)
                .stick(stick)
                .build());
    }
}
