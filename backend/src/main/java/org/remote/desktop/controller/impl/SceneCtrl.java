package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.controller.SceneApi;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.vto.GamepadEventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.provider.impl.LocalXdoSceneProvider;
import org.remote.desktop.service.TriggerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class SceneCtrl implements SceneApi {

    private final SceneDao sceneDao;
    private final TriggerService triggerService;
    private final LocalXdoSceneProvider xdoSceneProvider;

    @GetMapping("allScenes")
    public List<SceneVto> getAllScenes() {
        return sceneDao.getAllSceneVtos();
    }

    @GetMapping("inherents/{sceneId}")
    public List<GamepadEventVto> getInherentsForScene(@PathVariable("sceneId") long sceneId) {
        return sceneDao.getInherentsRecurcivelyFor(sceneId);
    }

    public String getCurrentSceneName() {
        return xdoSceneProvider.tryGetCurrentName();
    }

    @PostMapping("saveScene")
    public Long saveScene(@RequestBody SceneVto sceneVto) {
        return sceneDao.save(sceneVto);
    }

    @PutMapping("updateScene")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateScene(@RequestBody SceneVto sceneVto) {
        sceneDao.update(sceneVto);
    }

    @PutMapping("updateGamepadEvent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGamepadAction(@RequestBody GamepadEventVto gamepadEventVto) {
        sceneDao.update(gamepadEventVto);
    }

    @PutMapping("updateXdoAction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGamepadAction(@RequestBody XdoActionVto xdoActionVto) {
        sceneDao.update(xdoActionVto);
    }

    @PostMapping("saveXdoAction")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Long> saveXdoAction(@RequestBody XdoActionVto xdoActionVto) {
        return sceneDao.save(xdoActionVto);
    }

    @PostMapping("saveGamepadEvent")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveGamepadEvent(@RequestBody GamepadEventVto gamepadEventVto) {
        return sceneDao.save(gamepadEventVto);
    }

    @GetMapping("xdoStrokes")
    public List<String> getAllCurrentXdoStrokes() {
        return sceneDao.getAllCurrentXdoStrokes();
    }

    @GetMapping("getTriggers")
    public List<String> getAllTriggers() {
        return triggerService.getAllLogicalTriggerNames();
    }

    @DeleteMapping("removeXdoAction")
    public void removeXdoAction(@RequestBody Long xdoActionId) {
        sceneDao.removeXdoAction(xdoActionId);
    }

    @DeleteMapping("removeGamepadEvent")
    public void removeGamepadEvent(@RequestBody Long gPadId) {
        sceneDao.removeGamepadEvent(gPadId);
    }

    @DeleteMapping("removeScene")
    public void removeScene(@RequestBody Long sceneId) {
        sceneDao.removeScene(sceneId);
    }
}
