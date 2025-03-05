package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.vto.GamepadEventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SceneCtrl {

    private final SceneDao sceneDao;

    @GetMapping("allScenes")
    public List<SceneVto> getAllScenes() {
        return sceneDao.getAllSceneVtos();
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
        try {
            sceneDao.update(xdoActionVto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("saveXdoAction")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveXdoAction(@RequestBody XdoActionVto xdoActionVto) {
        return sceneDao.save(xdoActionVto);
    }

    @PostMapping("saveGamepadEvent")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveGamepadEvent(@RequestBody GamepadEventVto gamepadEventVto) {
        return sceneDao.save(gamepadEventVto);
    }
}
