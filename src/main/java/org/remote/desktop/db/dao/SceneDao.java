package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.db.repository.GamepadEventRepository;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.GamepadEventMapper;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.mapper.XdoActionMapper;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.vto.GamepadEventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class SceneDao {

    public static final String SCENE_CACHE_NAME = "scenes";
    public static final String WINDOW_SCENE_CACHE_NAME = "mapped_scenes";
    public static final String SCENE_NAME_CACHE_NAME = "scene_name";
    public static final String SCENE_AXIS_CACHE_NAME = "scene_axis_assign";

    private final SceneRepository sceneRepository;
    private final GamepadEventRepository gamepadEventRepository;
    private final XdoActionRepository xdoActionRepository;

    private final SceneMapper sceneMapper;
    private final XdoActionMapper xdoActionMapper;
    private final GamepadEventMapper gamepadEventMapper;


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
    public void update(GamepadEventDto gamepadEventDto) {
        try {
            Optional.of(gamepadEventDto)
                    .map(GamepadEventDto::getId)
                    .flatMap(gamepadEventRepository::findById)
                    .ifPresent(gamepadEventMapper.updater(gamepadEventDto));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, WINDOW_SCENE_CACHE_NAME, SCENE_NAME_CACHE_NAME}, allEntries = true)
    public void remove(GamepadEventDto vto) {
        try {
            Optional.of(vto)
                    .map(q -> gamepadEventMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(gamepadEventRepository::delete);
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
    public GamepadEventDto save(GamepadEventDto GamepadEventDto) {
        return Optional.of(GamepadEventDto)
                .map(q -> gamepadEventMapper.map(q, new CycleAvoidingMappingContext()))
                .map(gamepadEventRepository::save)
                .map(q -> gamepadEventMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }

    GPadEventStreamService.RecursiveScraper<GamepadEvent> scraper = new GPadEventStreamService.RecursiveScraper<>();

    public List<SceneVto> getAllSceneVtos() {
        return sceneRepository.findAll().stream()
                .map(sceneMapper::map)
                .map(q -> Optional.ofNullable(nullableRepoOp(q.getInheritsNameFk(), sceneRepository::findById))
                        .map(p -> scraper.scrapeActionsRecursive(p))
                        .map(gamepadEventMapper::map)
                        .map(q::withInheritedGamepadEvents)
                        .orElse(q))
                .toList();
    }

    public void update(XdoActionVto vto) {
        Optional.of(vto)
                .map(XdoActionVto::getId)
                .flatMap(xdoActionRepository::findById)
                .ifPresent(xdoActionMapper.update(vto, nullableRepoOp(vto.getGamepadEventFk(), gamepadEventRepository::findById)));
    }

    public void update(GamepadEventVto vto) {
        Optional.of(vto)
                .map(GamepadEventVto::getId)
                .flatMap(gamepadEventRepository::findById)
                .ifPresent(gamepadEventMapper.update(vto,
                        nullableRepoOp(vto.getParentSceneFk(), sceneRepository::findById),
                        nullableRepoOp(vto.getNextSceneNameFk(), sceneRepository::findById)
                ));
    }

    public void update(SceneVto vto) {
        Optional.of(vto)
                .map(SceneVto::getName)
                .flatMap(sceneRepository::findById)
                .ifPresent(sceneMapper.update(vto, nullableRepoOp(vto.getInheritsNameFk(), sceneRepository::findById)));
    }

    public String save(SceneVto vto) {
        return Optional.of(vto)
                .map(sceneMapper.mapToEntity(nullableRepoOp(vto.getInheritsNameFk(), sceneRepository::findById)))
                .map(sceneRepository::save)
                .map(Scene::getName)
                .orElseThrow();
    }

    public Long save(GamepadEventVto vto) {
        return Optional.of(vto)
                .map(gamepadEventMapper.map(nullableRepoOp(vto.getParentSceneFk(), sceneRepository::findById),
                        nullableRepoOp(vto.getNextSceneNameFk(), sceneRepository::findById))
                )
                .map(gamepadEventRepository::save)
                .map(GamepadEvent::getId)
                .orElseThrow();
    }

    public Mono<Long> save(XdoActionVto vto) {
        return Mono.just(vto)
                .map(xdoActionMapper.map(nullableRepoOp(vto.getGamepadEventFk(), gamepadEventRepository::findById)))
                .map(xdoActionRepository::save)
                .map(XdoAction::getId);
    }

    public void removeXdoAction(Long id) {
        xdoActionRepository.deleteById(id);
    }

    public void removeGamepadEvent(Long id) {
        gamepadEventRepository.deleteById(id);
    }

    public void removeScene(String name) {
        sceneRepository.deleteById(name);
    }

    static <T, R> R nullableRepoOp(T id, Function<T, Optional<R>> function) {
        return Optional.ofNullable(id)
                .flatMap(function)
                .orElse(null);
    }
}
