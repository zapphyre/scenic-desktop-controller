package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.mapper.ActionMapper;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.mapper.XdoActionMapper;
import org.remote.desktop.model.dto.GPadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.db.repository.GPadEventRepository;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Transactional(isolation = Isolation.SERIALIZABLE)
@RequiredArgsConstructor
public class SceneDao {

    public static final String SCENE_CACHE_NAME = "scenes";
    public static final String WINDOW_SCENE_CACHE_NAME = "mapped_scenes";
    public static final String SCENE_NAME_CACHE_NAME = "scene_name";
    public static final String SCENE_AXIS_CACHE_NAME = "scene_axis_assign";

    private final SceneRepository sceneRepository;
    private final GPadEventRepository actionRepository;
    private final XdoActionRepository xdoActionRepository;

    private final SceneMapper sceneMapper;
    private final XdoActionMapper xdoActionMapper;
    private final ActionMapper actionMapper;


    @Cacheable(SCENE_CACHE_NAME)
    public List<SceneDto> getAllScenes() {
        return sceneRepository.findAll().stream()
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .toList();
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME, SCENE_AXIS_CACHE_NAME}, allEntries = true)
    public List<SceneDto> saveAll(Collection<SceneDto> scenes) {
        return scenes.stream()
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .map(sceneRepository::save)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .toList();
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME, SCENE_AXIS_CACHE_NAME}, allEntries = true)
    public SceneDto save(SceneDto sceneDto) {
        return Optional.of(sceneDto)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .map(sceneRepository::save)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }

    @Cacheable(SCENE_CACHE_NAME)
    public SceneDto getScene(String sceneName) {
        return sceneRepository.findById(sceneName)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }

    @Cacheable(SCENE_CACHE_NAME)
    public SceneDto getSceneForWindowNameOrBase(String sceneName) {
        System.out.println("getting scene " + sceneName);

        List<Scene> bySceneContain = sceneRepository.findBySceneContain(sceneName);

        if (bySceneContain.size() > 1)
            log.info("Found more than one scene with name; scenes found: {}" + sceneName, bySceneContain);

        if (bySceneContain.isEmpty())
            return getScene("Base");

        return sceneMapper.map(bySceneContain.getFirst(), new CycleAvoidingMappingContext());
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public XdoActionDto save(XdoActionDto actionVto) {
        XdoActionDto xdoActionVto = null;
        try {
            xdoActionVto = Optional.of(actionVto)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .map(xdoActionRepository::saveAndFlush)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .orElseThrow();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xdoActionVto;
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void update(XdoActionDto actionVto) {
        try {
            Optional.of(actionVto)
                    .map(XdoActionDto::getId)
                    .flatMap(xdoActionRepository::findById)
                    .ifPresent(xdoActionMapper.updater(actionVto));
            xdoActionRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void remove(XdoActionDto vto) {
        try {
            Optional.of(vto)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(xdoActionRepository::delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void update(GPadEventDto gPadEventDto) {
        try {
            Optional.of(gPadEventDto)
                    .map(GPadEventDto::getId)
                    .flatMap(actionRepository::findById)
                    .ifPresent(actionMapper.updater(gPadEventDto));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void remove(GPadEventDto vto) {
        try {
            Optional.of(vto)
                    .map(q -> actionMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(actionRepository::delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME, SCENE_AXIS_CACHE_NAME}, allEntries = true)
    public void update(SceneDto sceneDto) {
        try {
            Optional.of(sceneDto)
                    .map(SceneDto::getName)
                    .flatMap(sceneRepository::findById)
                    .ifPresent(sceneMapper.updater(sceneDto));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME, SCENE_AXIS_CACHE_NAME}, allEntries = true)
    public void remove(SceneDto vto) {
        try {
            Optional.of(vto)
                    .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(sceneRepository::delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public GPadEventDto save(GPadEventDto GPadEventDto) {
        return Optional.of(GPadEventDto)
                .map(q -> actionMapper.map(q, new CycleAvoidingMappingContext()))
                .map(actionRepository::save)
                .map(q -> actionMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }
}
