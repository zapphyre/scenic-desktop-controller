package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.Mode;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.db.repository.ModeRepository;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.EventMapper;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.util.RecursiveScraper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class SceneDao {

    public static final String SCENE_CACHE_NAME = "scenes";
    public static final String SCENE_INHERENTS_FOR_SCENE = "inherents";
    public static final String SCENE_CACHE_NAME_CONTAINING = "window_containing";
    public static final String SCENE_LIST_CACHE_NAME = "many_scenes";
    public static final String SCENE_ACTIONS_CACHE_NAME = "scene_actions";
    public static final String SCENE_AXIS_CACHE_NAME = "scene_axis_assign";

    private final RecursiveScraper<Event, Scene> scraper = new RecursiveScraper<>();

    private final SceneRepository sceneRepository;
    private final ModeRepository modeRepository;

    private final SceneMapper sceneMapper;
    private final EventMapper eventMapper;

    public SceneDto getScene(String sceneName) {
        return sceneRepository.findByName(sceneName)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .orElse(null);
    }

    public SceneDto getSceneForWindowNameOrBase(String sceneName, String mode) {
        List<Scene> bySceneContain = sceneRepository.findBySceneContain(sceneName, mode);

//        if (bySceneContain.size() > 1)
//            log.info("Found more than one scene with name; scenes found: {}" + sceneName, bySceneContain);

        if (bySceneContain.isEmpty())
            return getScene("Base");

        return sceneMapper.map(bySceneContain.getFirst(), new CycleAvoidingMappingContext());
    }

    public List<SceneDto> getAllMatchingScenes(String sceneName, String mode) {
        return sceneRepository.findBySceneContain(sceneName, mode).stream()
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .toList();
    }

    public List<SceneVto> getAllSceneVtos(String mode) {
        return sceneRepository.findAllByMode_AdapterMode(mode).stream()
                .map(sceneMapper::map)
                .map(q -> sceneRepository.findAllById(q.getInheritsIdFk()).stream()
                        .map(scraper::scrapeActionsRecursiveWithCurrent)
                        .map(eventMapper::map)
                        .flatMap(Collection::stream)
                        .collect(collectingAndThen(toList(), q::withInheritedGamepadEvents)))
                .toList();
    }

    public SceneVto getSceneVtoBy(String sceneName) {
        return sceneRepository.findByName(sceneName)
                .map(sceneMapper::map)
                .orElse(null);
    }

    public void update(SceneVto vto) {
        Optional.of(vto)
                .map(SceneVto::getId)
                .flatMap(sceneRepository::findById)
                .ifPresent(sceneMapper.update(vto, Optional.ofNullable(vto.getInheritsIdFk()).map(sceneRepository::findAllById).orElseGet(Collections::emptyList)));
    }

    public void update(SceneDto dto) {
        Optional.of(dto)
                .map(SceneDto::getId)
                .flatMap(sceneRepository::findById)
                .ifPresent(sceneMapper.update(dto));
    }

    public Long createForId(SceneVto vto) {
        return create(vto).getId();
    }

    public SceneVto create(SceneVto vto) {
        return Optional.of(vto)
                .map(sceneMapper.mapWithInherents(safeRepo(sceneRepository::findAllById, vto.getInheritsIdFk())))
                .map(q -> q.withMode(modeRepository.findByAdapterMode(vto.getMode())))
                .map(sceneRepository::save)
                .map(sceneMapper::map)
                .orElseThrow();
    }

    SceneDto create(SceneDto dto) {
        return Optional.of(dto)
                .map(sceneMapper.mapWithMode(modeRepository.findByAdapterMode(dto.getMode().getAdapterMode())))
                .map(sceneRepository::save)
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseThrow();
    }

    public void delete(Long name) {
        sceneRepository.deleteById(name);
    }

    <R> List<R> safeRepo(Function<Iterable<Long>, List<R>> repoFun, Iterable<Long> ids) {
        return Optional.ofNullable(ids)
                .map(repoFun)
                .orElse(List.of());
    }

    public SceneDto getModeDefault(String name) {
        return sceneRepository.findByName(name) // maybe create some derivation of scene name not the exact mode name
                .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                .orElseGet(() -> createDefaultSceneForMode(name));
    }

    public SceneDto createDefaultSceneForMode(String name) {
        Mode mode = Mode.builder()
                .adapterMode(name)
                .build();
        mode = modeRepository.save(mode);

        Scene scene = Scene.builder()
                .name(name)
                .mode(mode)
                .build();

        return sceneMapper.map(sceneRepository.save(scene), new CycleAvoidingMappingContext());
    }
}
