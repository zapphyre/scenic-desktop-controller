package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EQualificationType;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.mapper.ActivatorGroupingEventMapper;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.ButtonEventDto;
import org.remote.desktop.model.dto.EventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.remote.desktop.util.RecursiveScraper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.remote.desktop.util.FluxUtil.laterMerger;

@Slf4j
@Service
@RequiredArgsConstructor
public class GPadEventStreamService {

    private final SceneService sceneService;
    private final ButtonPressMapper buttonPressMapper;
    private final XdoSceneService xdoSceneService;
    private final ActivatorGroupingEventMapper activatorGroupingEventMapper;

    private final RecursiveScraper<EventDto, SceneDto> scraper = new RecursiveScraper<>();

    public Predicate<ButtonEventDto> triggerAndModifiersSameAsClick(ButtonActionDef click) {
        return q -> sameAsClick(click).test(q.getTrigger()) ||
                q.getModifiers().stream()
                        .map(Enum::name)
                        .anyMatch(sameAsClick(click));
    }

    Predicate<String> sameAsClick(ButtonActionDef click) {
        return click.getTrigger()::equals;
    }

    @Cacheable(SceneDao.SCENE_ACTIONS_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
        return ofNullable(windowName)
                .map(sceneService::getSceneForWindowNameOrBase)
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

    @Cacheable(SceneDao.SCENE_ACTIONS_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> extractInheritedActions(SceneDto sceneDto) {
        return of(sceneDto)
                .map(scraper::scrapeActionsRecursive)
                .orElseThrow().stream()
                .map(activatorGroupingEventMapper::groupByActivator)
                .flatMap(Collection::stream)
                .map(buttonPressMapper.map(sceneDto))
                .collect(toMap(SceneBtnActions::action, buttonPressMapper::map, laterMerger()));
    }

    public SceneDto sceneNow() {
        return xdoSceneService.isSceneForced() ?
                xdoSceneService.getForcedScene() :
                sceneService.getSceneForWindowNameOrBase(xdoSceneService.tryGetCurrentName());
    }

    public boolean isCurrentClickQualificationSceneRelevant(ButtonActionDef click) {
        return of(sceneNow())
                .map(isIncomingQualificatorRelevantForCurrentScene(click))
                .orElse(false);
    }

    public Function<SceneDto, Boolean> isIncomingQualificatorRelevantForCurrentScene(ButtonActionDef click) {
        return scene -> Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> scraper.scrapeActionsRecursive(scene).stream()
                        .map(EventDto::getButtonEvent)
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
            qualificationReceived.addAll(List.of(
                    EQualificationType.RELEASE,
                    EQualificationType.LONG,
                    EQualificationType.MULTIPLE
            ));

        // extended for longPress == true b/c on the scene that max qualification is long, even untruthy long matches
        // and it would generate -release- purging qualif filter element unjustly as action itself was not longPress,
        // only it was recognized as 'relevant' as long since it was max scene qualif and there was long action configured
        if (click.getQualified() == EQualificationType.LONG && click.isLongPress())
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
