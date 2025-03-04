package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SceneCrtl {

    private final SceneDao sceneDao;

    @GetMapping("allScenes")
    public List<SceneDto> getAllScenes() {
        return sceneDao.getAllScenes();
    }

    @PutMapping("updateScene")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateScene(@RequestBody SceneDto sceneDto) {
        sceneDao.update(sceneDto);
    }

    @PutMapping("updateGamepadEvent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGamepadAction(@RequestBody GamepadEventDto gamepadEvent) {
        sceneDao.update(gamepadEvent);
    }

    @PutMapping("updateXdoAction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGamepadAction(@RequestBody XdoActionDto xdoActionDto) {
        sceneDao.update(xdoActionDto);
    }

    @PostMapping("saveXdoAction")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveXdoAction(@RequestBody XdoActionDto xdoActionDto) {
        return sceneDao.save(xdoActionDto).getId();
    }

    @PostMapping("saveGamepadEvent")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveGamepadEvent(@RequestBody GamepadEventDto gamepadEventDto) {
        return sceneDao.save(gamepadEventDto).getId();
    }
}
