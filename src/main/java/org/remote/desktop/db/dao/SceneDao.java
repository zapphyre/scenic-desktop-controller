package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.mapper.ActionMapper;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.mapper.XdoActionMapper;
import org.remote.desktop.model.vto.GPadEventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.db.repository.GPadEventRepository;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.remote.desktop.ui.component.SaveNotifiaction;
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
    public List<SceneVto> getAllScenes() {
        return sceneRepository.findAll().stream()
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .toList();
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME, SCENE_AXIS_CACHE_NAME}, allEntries = true)
    public List<SceneVto> saveAll(Collection<SceneVto> scenes) {
        return scenes.stream()
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .map(sceneRepository::save)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .toList();
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME, SCENE_AXIS_CACHE_NAME}, allEntries = true)
    public SceneVto save(SceneVto sceneVto) {
        return Optional.of(sceneVto)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .map(sceneRepository::save)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }

    @Cacheable(SCENE_CACHE_NAME)
    public SceneVto getScene(String sceneName) {
        return sceneRepository.findById(sceneName)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }

    @Cacheable(SCENE_CACHE_NAME)
    public SceneVto getSceneForWindowNameOrBase(String sceneName) {
        System.out.println("getting scene " + sceneName);

        List<Scene> bySceneContain = sceneRepository.findBySceneContain(sceneName);

        if (bySceneContain.size() > 1)
            log.info("Found more than one scene with name; scenes found: {}" + sceneName, bySceneContain);

        if (bySceneContain.isEmpty())
            return getScene("Base");

        return sceneMapper.map(bySceneContain.getFirst(), new CycleAvoidingMappingContext());
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public XdoActionVto save(XdoActionVto actionVto) {
        XdoActionVto xdoActionVto = null;
        try {
            xdoActionVto = Optional.of(actionVto)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .map(xdoActionRepository::saveAndFlush)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .orElseThrow();
            SaveNotifiaction.success("saved");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }

        return xdoActionVto;
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void update(XdoActionVto actionVto) {
        try {
            Optional.of(actionVto)
                    .map(XdoActionVto::getId)
                    .flatMap(xdoActionRepository::findById)
                    .ifPresent(xdoActionMapper.updater(actionVto));
            xdoActionRepository.flush();
            SaveNotifiaction.success("xDoActoin updated");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void remove(XdoActionVto vto) {
        try {
            Optional.of(vto)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(xdoActionRepository::delete);
            SaveNotifiaction.success("removed");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void update(GPadEventVto gPadEventVto) {
        try {
            Optional.of(gPadEventVto)
                    .map(GPadEventVto::getId)
                    .flatMap(actionRepository::findById)
                    .ifPresent(actionMapper.updater(gPadEventVto));
            SaveNotifiaction.success("gPadEvent updated");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void remove(GPadEventVto vto) {
        try {
            Optional.of(vto)
                    .map(q -> actionMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(actionRepository::delete);
            SaveNotifiaction.success("removed");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME, SCENE_AXIS_CACHE_NAME}, allEntries = true)
    public void update(SceneVto sceneVto) {
        try {
            Optional.of(sceneVto)
                    .map(SceneVto::getName)
                    .flatMap(sceneRepository::findById)
                    .ifPresent(sceneMapper.updater(sceneVto));
            SaveNotifiaction.success("updated");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME, SCENE_AXIS_CACHE_NAME}, allEntries = true)
    public void remove(SceneVto vto) {
        try {
            Optional.of(vto)
                    .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(sceneRepository::delete);
            SaveNotifiaction.success("removed");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public GPadEventVto save(GPadEventVto GPadEventVto) {
        return Optional.of(GPadEventVto)
                .map(q -> actionMapper.map(q, new CycleAvoidingMappingContext()))
                .map(actionRepository::save)
                .map(q -> actionMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }
}
