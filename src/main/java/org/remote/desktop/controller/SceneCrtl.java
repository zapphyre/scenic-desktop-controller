package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SceneCrtl {

    private final SceneDao sceneDao;

    @GetMapping("allScenes")
    public Mono<List<SceneDto>> getAllScenes() {
        return Mono.just(sceneDao.getAllScenes());
    }

    @PutMapping(value = "updateScene", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateScene(@RequestBody SceneDto sceneDto) {
        sceneDao.update(sceneDto);
    }
}
