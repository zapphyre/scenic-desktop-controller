package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.remote.desktop.db.dao.SceneDao.*;

@Service
@RequiredArgsConstructor
public class SceneService {

    private final SceneDao sceneDao;

    @Cacheable(SCENE_LIST_CACHE_NAME)
    public List<SceneVto> getAllSceneVtos() {
        return sceneDao.getAllSceneVtos();
    }

    @Cacheable(SCENE_CACHE_NAME)
    public SceneDto getScene(String sceneName) {
        return sceneDao.getScene(sceneName);
    }

    @Cacheable(SCENE_CACHE_NAME_CONTAINING)
    public SceneDto getSceneForWindowNameOrBase(String sceneName) {
        return sceneDao.getSceneForWindowNameOrBase(sceneName);
    }

    public Long create(SceneVto sceneVto) {
        return sceneDao.create(sceneVto);
    }

    public void update(SceneVto sceneVto) {
        sceneDao.update(sceneVto);
    }

    public void delete(Long sceneId) {
        sceneDao.delete(sceneId);
    }
}
