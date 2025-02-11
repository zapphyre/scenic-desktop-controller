package org.remote.desktop.service;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.model.ButtonActionDef;
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


@Service
@Transactional
@RequiredArgsConstructor
public class SceneService {

    private final String SCENE_CACHE_NAME = "scenes";
    private final String MAPPED_CACHE_NAME = "mapped_scenes";
    private final String WINDOW_SCENE_CACHE_NAME = "mapped_scenes";

    private final SceneRepository sceneRepository;
    private final SceneMapper sceneMapper;
    private final CacheManager cacheManager;

    @Cacheable(SCENE_CACHE_NAME)
    public List<SceneVto> getScenes() {
        return Optional.of(sceneRepository.findAll())
                .map(q -> sceneMapper.mapToVdos(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }

    //    @CacheEvict({SCENE_CACHE_NAME, MAPPED_CACHE_NAME, WINDOW_SCENE_CACHE_NAME})
    public List<SceneVto> saveAll(List<SceneVto> scenes) {
        Stream.of(SCENE_CACHE_NAME, MAPPED_CACHE_NAME, WINDOW_SCENE_CACHE_NAME)
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(Cache::invalidate);

        return Optional.of(scenes)
                .map(q -> sceneMapper.mapToEntities(q, new CycleAvoidingMappingContext()))
                .map(sceneRepository::saveAll)
                .map(q -> sceneMapper.mapToVdos(q, new CycleAvoidingMappingContext()))
                .orElseThrow();

    }

    @Cacheable(MAPPED_CACHE_NAME)
    public Map<String, Map<ButtonActionDef, List<XdoActionVto>>> mappedActions() {
        return getScenes().stream()
                .flatMap(q -> q.getActions().stream()
                        .map(p -> new SceneBtnActions(q.getWindowName(), ButtonActionDef.builder()
                                .trigger(p.getTrigger())
                                .modifiers(p.getModifiers())
                                .build(), p.getActions())))
                .collect(toMap(SceneBtnActions::name, q -> Map.of(q.buttonActionDef, q.actions)));
    }

    @Cacheable(WINDOW_SCENE_CACHE_NAME)
    public Map<ButtonActionDef, List<XdoActionVto>> relativeWindowNameActions(String windowName) {
        return getScenes().stream()
                .filter(q -> windowName.contains(q.getWindowName()))
                .flatMap(q -> q.getActions().stream()
                        .map(p -> new SceneBtnActions(q.getWindowName(), ButtonActionDef.builder()
                                .trigger(p.getTrigger())
                                .modifiers(p.getModifiers())
                                .build(), p.getActions())))
                .collect(toMap(SceneBtnActions::buttonActionDef, q -> q.actions));
    }

    record SceneBtnActions(String name, ButtonActionDef buttonActionDef, List<XdoActionVto> actions) {
    }
}
