package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
