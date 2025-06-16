package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.TriggerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/scene")
@RequiredArgsConstructor
public class SceneCtrl {

    private final TriggerService triggerService;
    private final SceneService sceneService;

    @GetMapping("all")
    public List<SceneVto> getAllScenes() {
        return sceneService.getAllSceneVtos();
    }

    @PostMapping
    public Long saveScene(@RequestBody SceneVto sceneVto) {
        return sceneService.create(sceneVto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateScene(@RequestBody SceneVto sceneVto) {
        sceneService.update(sceneVto);
    }

    @GetMapping("triggers")
    public List<String> getAllTriggers() {
        return triggerService.getAllLogicalTriggerNames();
    }

    @DeleteMapping("{sceneId}")
    public void removeScene(@PathVariable("sceneId") Long sceneId) {
        sceneService.delete(sceneId);
    }
}
