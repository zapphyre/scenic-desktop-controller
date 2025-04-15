package org.remote.desktop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EQualificationType;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.remote.desktop.util.RecursiveScraper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationListener;
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
    private final RecursiveScraper<GamepadEventDto, SceneDto> scraper = new RecursiveScraper<>();

    public Predicate<GamepadEventDto> triggerAndModifiersSameAsClick(ButtonActionDef click) {
        return q -> sameAsClick(click).test(q.getTrigger()) ||
                q.getModifiers().stream()
                        .map(Enum::name)
                        .anyMatch(sameAsClick(click));
    }

    public Predicate<String> sameAsClick(ButtonActionDef click) {
        return click.getTrigger()::equals;
    }

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
        return Optional.ofNullable(windowName)
                .map(sceneDao::getSceneForWindowNameOrBase)
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> extractInheritedActions(SceneDto sceneDto) {
        return scraper.scrapeActionsRecursive(sceneDto).stream()
                .map(buttonPressMapper.map(sceneDto))
                .collect(toMap(SceneBtnActions::action, buttonPressMapper::map, (p, q) -> q));
    }

    public boolean isCurrentClickQualificationSceneRelevant(ButtonActionDef click) {
        SceneDto scene = sceneStateRepository.isSceneForced() ?
                sceneStateRepository.getForcedScene() :
                sceneDao.getSceneForWindowNameOrBase(sceneStateRepository.tryGetCurrentName());

        return sceneClickQualificationRelevant(click, scene);
    }

    EQualificationType last = EQualificationType.MULTIPLE;

    public boolean sceneClickQualificationRelevant(ButtonActionDef click, SceneDto scene) {
        return Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> scraper.scrapeActionsRecursive(scene).stream()
                        .filter(triggerAndModifiersSameAsClick(click))
                        .anyMatch(q.getPredicate()))
                .findFirst()
                .map(EQualifiedSceneDict::getQualifierType)
                .map(q -> q == click.getQualified())
                .orElse(false);
    }

    private final Set<EQualificationType> qualificationReceived = new HashSet<>();
    public void computeRemainderFilter(ButtonActionDef click) {
        if (click.getQualified() == EQualificationType.PUSH)
            qualificationReceived.addAll(Arrays.asList(
                    EQualificationType.RELEASE,
                    EQualificationType.LONG,
                    EQualificationType.MULTIPLE
            ));

        if (click.getQualified() == EQualificationType.LONG)
            qualificationReceived.add(EQualificationType.RELEASE);

        if (click.getQualified() == EQualificationType.RELEASE)
            qualificationReceived.add(EQualificationType.LONG);

        if (click.getQualified() != EQualificationType.MULTIPLE)
            qualificationReceived.add(EQualificationType.MULTIPLE);
    }

    public boolean consumeEventLeftovers(ButtonActionDef def) {
        return !qualificationReceived.remove(def.getQualified());
    }

    public record SceneBtnActions(String windowName, ActionMatch action, List<XdoActionDto> actions,
                                  SceneDto nextScene, SceneDto currentScene, SceneDto eventSourceScene) {
    }
}
