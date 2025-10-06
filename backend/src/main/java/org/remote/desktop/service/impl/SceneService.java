package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.mapper.GamepadMapper;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.SceneVto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

import static org.remote.desktop.db.dao.SceneDao.*;

@Service
@RequiredArgsConstructor
public class SceneService {

    private final SceneDao sceneDao;
    private final ModeService  modeService;
    private final GamepadMapper gamepadMapper;

//    @Cacheable(SCENE_LIST_CACHE_NAME)
    public List<SceneVto> getAllSceneVtos(String mode) {
        return sceneDao.getAllSceneVtos(mode);
    }

    @Cacheable(SCENE_CACHE_NAME)
    public SceneDto getScene(String sceneName) {
        return sceneDao.getScene(sceneName);
    }

    public SceneDto getSystemScene() {
        return sceneDao.getScene("system");
    }

//    @Cacheable(SCENE_CACHE_NAME_CONTAINING)
    public Function<String, SceneDto> getSceneForModeAndWindowNameOrBase(GamepadDto device) {
        return getSceneForModeAndWindowNameOrBase(gamepadMapper.map(device));
    }

    public Function<String, SceneDto> getSceneForModeAndWindowNameOrBase(GamepadDevice device) {
        return getSceneForModeAndWindowNameOrBase(false, device);
    }

    @Cacheable(SCENE_CACHE_NAME_CONTAINING)
    public Function<String, SceneDto> getSceneForModeAndWindowNameOrBase(boolean ignoreMode, GamepadDevice device) {
        return q -> modeService.isCurrentGamepadModeScenic(device) || ignoreMode ?
                sceneDao.getSceneForWindowNameOrBase(q, modeService.getCurrentModeNameFor(device)) :
                sceneDao.getModeDefault(modeService.getCurrentModeNameFor(device));
    }

    @CacheEvict(value = {SCENE_LIST_CACHE_NAME,  SCENE_CACHE_NAME_CONTAINING, SCENE_CACHE_NAME}, allEntries = true)
    public Long create(SceneVto sceneVto) {
        return sceneDao.createForId(sceneVto);
    }

    @CacheEvict(value = {SCENE_LIST_CACHE_NAME,  SCENE_CACHE_NAME_CONTAINING, SCENE_CACHE_NAME}, allEntries = true)
    public void update(SceneVto sceneVto) {
        sceneDao.update(sceneVto);
    }

    @CacheEvict(value = {SCENE_LIST_CACHE_NAME,  SCENE_CACHE_NAME_CONTAINING, SCENE_CACHE_NAME}, allEntries = true)
    public void update(SceneDto sceneDto) {
        sceneDao.update(sceneDto);
    }

    @CacheEvict(value = {SCENE_LIST_CACHE_NAME,  SCENE_CACHE_NAME_CONTAINING, SCENE_CACHE_NAME}, allEntries = true)
    public void delete(Long sceneId) {
        sceneDao.delete(sceneId);
    }

    public SceneVto getSceneByName(String name) {
        return sceneDao.getSceneVtoBy(name);
    }
}
