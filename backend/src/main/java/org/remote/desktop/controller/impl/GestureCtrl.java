package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.dto.rest.EStick;
import org.remote.desktop.model.dto.rest.NewGestureRequestDto;
import org.remote.desktop.model.vto.GesturePathVto;
import org.remote.desktop.model.vto.GestureVto;
import org.remote.desktop.service.impl.GestureService;
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

    @DeleteMapping("{id}")
    public void deleteGestureById(@PathVariable("id") Long id) {
        gestureService.deleteGesture(id);
    }

    @PutMapping("{id}/{name}")
    public void updateName(@PathVariable("id") Long id, @PathVariable("name") String name) {
        gestureService.updateName(id).accept(name);
    }
}
