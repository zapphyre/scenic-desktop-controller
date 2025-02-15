package org.remote.desktop.service;

import com.google.common.base.Strings;
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

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.remote.desktop.ui.view.component.SceneUi.scrapeActionsRecursive;


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

    @Cacheable(SCENE_CACHE_NAME)
    public SceneVto getScene(String sceneName) {
        return sceneRepository.findById(sceneName)
                .map(sceneMapper::map)
                .orElse(new SceneVto());
    }

    public SceneVto save(SceneVto sceneVto) {
        Stream.of(SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME)
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(Cache::invalidate);

        return Optional.of(sceneVto)
                .map(sceneMapper::map)
                .map(sceneRepository::save)
                .map(sceneMapper::map)
                .orElseThrow();
    }

    public List<SceneVto> saveAll(Collection<SceneVto> scenes) {
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
        return Optional.of(getScenes().stream()
                        .filter(q -> !Strings.isNullOrEmpty(q.getWindowName()))
                        .filter(q -> windowName.contains(q.getWindowName()))
                        .findFirst().orElseGet(() -> getScene("Base"))
                )
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

    public Map<ButtonActionDef, NextSceneXdoAction> extractInheritedActions(SceneVto sceneVto) {
        return Stream.of(scrapeActionsRecursive(sceneVto), sceneVto.getActions())
                .flatMap(Collection::stream)
                .map(p -> new SceneBtnActions(sceneVto.getWindowName(), ButtonActionDef.builder()
                                .trigger(p.getTrigger())
                                .modifiers(p.getModifiers())
                                .longPress(p.isLongPress())
                                .build(), p.getActions(), p.getNextScene()))
                .collect(toMap(SceneBtnActions::buttonActionDef, o -> new NextSceneXdoAction(o.nextScene, o.actions), (p, q) -> q));
    }

    record SceneBtnActions(String name, ButtonActionDef buttonActionDef, List<XdoActionVto> actions,
                           SceneVto nextScene) {
    }
}
