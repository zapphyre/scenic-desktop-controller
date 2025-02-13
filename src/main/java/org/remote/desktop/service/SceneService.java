package org.remote.desktop.service;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.model.XdoActionVto;
import org.remote.desktop.repository.SceneRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;


@Service
@Transactional
@RequiredArgsConstructor
public class SceneService {

    private final String SCENE_CACHE_NAME = "scenes";
    private final String WINDOW_SCENE_CACHE_NAME = "mapped_scenes";
    private final String SCENE_NAME_CACHE_NAME = "scene_name";

    private final SceneRepository sceneRepository;
    private final SceneMapper sceneMapper;
    private final CacheManager cacheManager;

    @Cacheable(SCENE_CACHE_NAME)
    public List<SceneVto> getScenes() {
        return sceneRepository.findAll().stream()
                .map(sceneMapper::map)
                .toList();
    }

    //    @CacheEvict({SCENE_CACHE_NAME, MAPPED_CACHE_NAME, WINDOW_SCENE_CACHE_NAME})
    public List<SceneVto> saveAll(List<SceneVto> scenes) {
        Stream.of(SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME)
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(Cache::invalidate);

        return scenes.stream()
                .map(sceneMapper::map)
                .map(sceneRepository::save)
                .map(sceneMapper::map)
                .toList();
    }

    @Cacheable(WINDOW_SCENE_CACHE_NAME)
    public Map<ButtonActionDef, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
        return getScenes().stream()
                .filter(q -> windowName.contains(q.getWindowName()))
                .findFirst()
                .map(this::extractActions)
                .orElse(Map.of());
    }

    @Cacheable(SCENE_NAME_CACHE_NAME)
    public Map<ButtonActionDef, NextSceneXdoAction> extractActions(SceneVto sceneVto) {
        return sceneVto.getActions().stream()
                .map(p -> new SceneBtnActions(sceneVto.getWindowName(), ButtonActionDef.builder()
                        .trigger(p.getTrigger())
                        .modifiers(p.getModifiers())
                        .build(), p.getActions(), p.getNextScene()))
                .collect(toMap(SceneBtnActions::buttonActionDef, q -> new NextSceneXdoAction(q.nextScene, q.actions)));
    }

    record SceneBtnActions(String name, ButtonActionDef buttonActionDef, List<XdoActionVto> actions, SceneVto nextScene) {
    }
}
