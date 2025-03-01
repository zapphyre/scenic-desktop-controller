package org.remote.desktop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EQualificationType;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.GPadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GPadEventStreamService {

    private final SceneDao sceneDao;
    private final ButtonPressMapper buttonPressMapper;
    private final SceneStateRepository sceneStateRepository;
    private final Set<ButtonActionDef> appliedCommands = new HashSet<>();

    private final String SERVICE_CACHE = "gpadEventStreamService";
    private final String SERVICE_CACHE_BUTTON_CLICK = "buttonClick_cache";

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
        return Optional.ofNullable(windowName)
                .map(sceneDao::getSceneForWindowNameOrBase)
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> extractInheritedActions(SceneDto sceneDto) {
        return scrapeActionsRecursive(sceneDto).stream()
                .map(buttonPressMapper.map(sceneDto.getWindowName()))
                .collect(toMap(SceneBtnActions::action, buttonPressMapper::map, (p, q) -> q));
    }

    public Predicate<GPadEventDto> triggerAndModifiersSameAsClick(ButtonActionDef click) {
        return q -> sameAsClick(click).test(q.getTrigger()) ||
                q.getModifiers().stream().anyMatch(sameAsClick(click));
    }

    public Predicate<EButtonAxisMapping> sameAsClick(ButtonActionDef click) {
        return q -> q == click.getTrigger();
    }

//    @Cacheable(SERVICE_CACHE_BUTTON_CLICK)
    public boolean getActuatorForScene(ButtonActionDef click) {
        if (click.getQualified() == EQualificationType.ARROW)
            return true;

        SceneDto scene = sceneStateRepository.isSceneForced() ?
                sceneStateRepository.getForcedScene() : sceneDao.getSceneForWindowNameOrBase(sceneStateRepository.tryGetCurrentName());

        EQualifiedSceneDict foundQualifier = Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> scrapeActionsRecursive(scene).stream()
                        .filter(triggerAndModifiersSameAsClick(click))
                        .anyMatch(q.getPredicate())
                )
                .findFirst()
                .orElse(EQualifiedSceneDict.FAST_CLICK);

        return foundQualifier.getQualifierType() == click.getQualified();
    }

    public boolean addAppliedCommand(ButtonActionDef click) {
        List<ButtonActionDef> defs = Arrays.stream(EQualificationType.values())
                .filter(q -> q.ordinal() > click.getQualified().ordinal())
                .map(click::withQualified)
                .toList();

        boolean b = appliedCommands.addAll(defs);

        return true; //forced b/c of arrows
    }

    public boolean withoutPreviousRelease(ButtonActionDef def) {
        return !appliedCommands.remove(def);
    }

    public static List<GPadEventDto> scrapeActionsRecursive(SceneDto sceneDto) {
        return sceneDto == null ? List.of() : scrapeActionsRecursive(sceneDto, new LinkedList<>());
    }

    public static List<GPadEventDto> scrapeActionsRecursive(SceneDto sceneDto, List<GPadEventDto> GPadEventDtos) {
//        if (sceneVto != null)
//            scrapeActionsRecursive(sceneVto.getInherits(), GPadEventVtos);

        Optional.ofNullable(sceneDto)
                .map(SceneDto::getGPadEvents)
                .ifPresent(GPadEventDtos::addAll);

        return GPadEventDtos;
    }

    public record SceneBtnActions(String windowName, ActionMatch action, List<XdoActionDto> actions,
                                  SceneDto nextScene) {
    }
}
