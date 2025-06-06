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
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.GamepadEventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.util.RecursiveScraper;
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
    public static final String SCENE_LIST_CACHE_NAME = "many_scenes";
    public static final String SCENE_ACTIONS_CACHE_NAME = "scene_actions";
    public static final String SCENE_AXIS_CACHE_NAME = "scene_axis_assign";

    private final RecursiveScraper<GamepadEvent, Scene> scraper = new RecursiveScraper<>();

    private final SceneRepository sceneRepository;
    private final GamepadEventRepository gamepadEventRepository;
    private final XdoActionRepository xdoActionRepository;

    private final SceneMapper sceneMapper;
    private final XdoActionMapper xdoActionMapper;
    private final GamepadEventMapper gamepadEventMapper;


    @Cacheable(SCENE_CACHE_NAME)
    public SceneDto getScene(String sceneName) {
        return sceneRepository.findByName(sceneName)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }

    @Cacheable(SCENE_CACHE_NAME)
    public SceneDto getSceneForWindowNameOrBase(String sceneName) {
        List<Scene> bySceneContain = sceneRepository.findBySceneContain(sceneName);

//        if (bySceneContain.size() > 1)
//            log.info("Found more than one scene with name; scenes found: {}" + sceneName, bySceneContain);

        if (bySceneContain.isEmpty())
            return getScene("Base");

        return sceneMapper.map(bySceneContain.getFirst(), new CycleAvoidingMappingContext());
    }

    public List<SceneDto> getAllMatchingScenes(String sceneName) {
        return sceneRepository.findBySceneContain(sceneName).stream()
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .toList();
    }

    @Cacheable(SCENE_LIST_CACHE_NAME)
    public List<SceneVto> getAllSceneVtos() {
        return sceneRepository.findAll().stream()
                .map(sceneMapper::map)
                .map(q -> {
                    List<GamepadEventVto> inherents = sceneRepository.findAllById(q.getInheritsIdFk()).stream()
                            .map(scraper::scrapeActionsRecursive)
                            .map(gamepadEventMapper::map)
                            .flatMap(Collection::stream)
                            .toList();

                    return q.withInheritedGamepadEvents(inherents);
                })
                .toList();
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public void update(XdoActionVto vto) {
        Optional.of(vto)
                .map(XdoActionVto::getId)
                .flatMap(xdoActionRepository::findById)
                .ifPresent(xdoActionMapper.update(vto, nullableRepoOp(vto.getGamepadEventFk(), gamepadEventRepository::findById)));
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public void update(GamepadEventVto vto) {
        Optional.of(vto)
                .map(GamepadEventVto::getId)
                .flatMap(gamepadEventRepository::findById)
                .ifPresent(gamepadEventMapper.update(vto,
                        nullableRepoOp(vto.getParentFk(), sceneRepository::findById),
                        nullableRepoOp(vto.getNextSceneFk(), sceneRepository::findById)
                ));
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public void update(SceneVto vto) {
        Optional.of(vto)
                .map(SceneVto::getId)
                .flatMap(sceneRepository::findById)
                .ifPresent(sceneMapper.update(vto, sceneRepository.findAllById(vto.getInheritsIdFk())));
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public Long save(SceneVto vto) {
        return Optional.of(vto)
                .map(sceneMapper.mapWithInherents(safeRepo(sceneRepository::findAllById, vto.getInheritsIdFk())))
                .map(sceneRepository::save)
                .map(Scene::getId)
                .orElseThrow();
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public Long save(GamepadEventVto vto) {
        return Optional.of(vto)
                .map(gamepadEventMapper.map(nullableRepoOp(vto.getParentFk(), sceneRepository::findById),
                        nullableRepoOp(vto.getNextSceneFk(), sceneRepository::findById))
                )
                .map(gamepadEventRepository::save)
                .map(GamepadEvent::getId)
                .orElseThrow();
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public Mono<Long> save(XdoActionVto vto) {
        return Mono.just(vto)
                .map(xdoActionMapper.map(nullableRepoOp(vto.getGamepadEventFk(), gamepadEventRepository::findById)))
                .map(xdoActionRepository::save)
                .map(XdoAction::getId);
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public void removeXdoAction(Long id) {
        xdoActionRepository.deleteById(id);
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public void removeGamepadEvent(Long id) {
        gamepadEventRepository.deleteById(id);
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public void removeScene(Long name) {
        sceneRepository.deleteById(name);
    }

    static <T, R> R nullableRepoOp(T id, Function<T, Optional<R>> function) {
        return Optional.ofNullable(id)
                .flatMap(function)
                .orElse(null);
    }

    public List<String> getAllCurrentXdoStrokes() {
        return xdoActionRepository.findAll().stream()
                .map(XdoAction::getKeyStrokes)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    <R> List<R> safeRepo(Function<Iterable<Long>, List<R>> repoFun, Iterable<Long> ids) {
        return Optional.ofNullable(ids)
                .map(repoFun)
                .orElse(List.of());
    }

    public List<GamepadEventVto> getInherentsRecurcivelyFor(long sceneId) {
        Scene scene = sceneRepository.findById(sceneId)
                .orElseThrow();

        return scraper.scrapeActionsRecursive(scene).stream()
                .map(gamepadEventMapper::map)
                .toList();
    }
}
