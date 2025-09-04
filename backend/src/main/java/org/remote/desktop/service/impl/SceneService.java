package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.SceneVto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.remote.desktop.db.dao.SceneDao.*;

@Service
@RequiredArgsConstructor
public class SceneService {

    private final SceneDao sceneDao;

//    @Cacheable(SCENE_LIST_CACHE_NAME)
    public List<SceneVto> getAllSceneVtos() {
        return sceneDao.getAllSceneVtos();
    }

    @Cacheable(SCENE_CACHE_NAME)
    public SceneDto getScene(String sceneName) {
        return sceneDao.getScene(sceneName);
    }

    public SceneDto getSystemScene() {
        return sceneDao.getScene("system");
    }

    @Cacheable(SCENE_CACHE_NAME_CONTAINING)
    public SceneDto getSceneForModeAndWindowNameOrBase(String sceneName) {
        return sceneDao.getSceneForWindowNameOrBase(sceneName);
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
}
