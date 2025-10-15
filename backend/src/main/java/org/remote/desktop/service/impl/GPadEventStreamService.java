package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EQualificationType;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.mapper.ActivatorGroupingEventMapper;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.CachedButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.ButtonEventDto;
import org.remote.desktop.model.dto.EventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.remote.desktop.util.RecursiveScraper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.zapphyre.function.FunHelper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GPadEventStreamService {

    private final SceneService sceneService;
    private final XdoSceneService xdoSceneService;
    private final ButtonPressMapper buttonPressMapper;
    private final ActivatorGroupingEventMapper activatorGroupingEventMapper;

    private final CacheManager  cacheManager;

    private final RecursiveScraper<EventDto, SceneDto> scraper = new RecursiveScraper<>();

    @Cacheable(SceneDao.SCENE_ACTIONS_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> relativeWindowNameActions(String windowName, GamepadDevice device) {
        return ofNullable(windowName)
                .map(sceneService.getSceneForModeAndWindowNameOrBase(device))
                .map(sceneDto -> extractInheritedActions(sceneDto, device))
                .orElseGet(Map::of);
    }

    @Cacheable(SceneDao.SCENE_ACTIONS_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> extractInheritedActions(SceneDto sceneDto, GamepadDevice device) {
        return of(sceneDto)
                .map(scraper.scrapeActionsRecursiveWithCurrentOn(sceneService.getSystemScene()))
                .orElseThrow().stream()
                .map(activatorGroupingEventMapper::groupByActivator)
                .flatMap(Collection::stream)
                .map(buttonPressMapper.map(sceneDto))
                .collect(toMap(SceneBtnActions::action, buttonPressMapper::map, laterMerger()));
    }

    public SceneDto sceneNow(GamepadDevice device) {
        return xdoSceneService.isSceneForced() ?
                xdoSceneService.getForcedScene() :
                sceneService.getSceneForModeAndWindowNameOrBase(device).apply(xdoSceneService.tryGetCurrentName());
    }

    public boolean isCurrentClickQualificationSceneRelevant(ButtonActionDef click) {
        SceneDto sceneDto = sceneNow(click.getDevice());
        Cache cache = cacheManager.getCache("klik");
        CachedButtonActionDef caClick = buttonPressMapper.mapCache(click);

        return Optional.ofNullable(cache)
                .map(q -> q.get(new SimpleKey(caClick, sceneDto), Boolean.class))
                .orElseGet(() -> of(sceneDto)
                        .map(isIncomingQualificatorRelevantForCurrentScene(click))
                        .map(funky(q -> cache.put(new SimpleKey(caClick, sceneDto), q)))
                        .map(funky(logFun("saving relevancy '{}' for scene: {} with trigger: {}", sceneDto.getName(), click.getTrigger())))
                        .orElse(false));
    }

    public Function<SceneDto, Boolean> isIncomingQualificatorRelevantForCurrentScene(ButtonActionDef click) {
        Function<SceneDto, Set<EventDto>> scrape = scraper.scrapeActionsRecursiveWithCurrentOn(sceneService.getSystemScene());

        return scene -> {
            Set<EventDto> eventsRelevantForCurrentClickModificators = Objects.isNull(click.getModifiers()) ?
                    Set.of() : scrape.apply(scene).stream()
                    .filter(modifiersRelevant(click))
                    .collect(Collectors.toSet());

            Predicate<EQualifiedSceneDict> longestQualifForRelevantEvents =
                    predicateForRelevantQualificators(eventsRelevantForCurrentClickModificators, click);

            return Arrays.stream(EQualifiedSceneDict.values())
                    .filter(longestQualifForRelevantEvents)
                    .findFirst()
                    .map(EQualifiedSceneDict::getQualifierType)
                    .map(q -> q == click.getQualified())
                    .orElse(false);
        };
    }

    Predicate<EventDto> modifiersRelevant(ButtonActionDef click) {
        return deepNonNull.and(modifiersEmpty(click).or(modifiersEqual(click)));
    }

    Predicate<EventDto> modifiersEqual(ButtonActionDef click) {
        return q -> q.getButtonEvent().getModifiers().equals(click.getModifiers());
    }

    Predicate<EventDto> modifiersEmpty(ButtonActionDef click) {
        return _ -> click.getModifiers().isEmpty();
    }

    Predicate<EventDto> deepNonNull = q -> Optional.ofNullable(q)
            .map(EventDto::getButtonEvent)
            .map(ButtonEventDto::getModifiers)
            .isPresent();

    Predicate<ButtonEventDto> triggerAndModifiersSameAsClick(ButtonActionDef click) {
        return sameAsClick(click).or(modifiersEqualAsClickTrigger(click));
    }

    Predicate<EQualifiedSceneDict> predicateForRelevantQualificators(Set<EventDto> evts, ButtonActionDef click) {
        return q -> evts.stream()
                .map(EventDto::getButtonEvent)
                .filter(Objects::nonNull)
                .filter(triggerAndModifiersSameAsClick(click))
                .anyMatch(q.getPredicate());
    }

    Predicate<ButtonEventDto> modifiersEqualAsClickTrigger(ButtonActionDef click) {
        return q -> q.getModifiers().stream()
                .map(Enum::name)
                .anyMatch(equalsTrigger(click));
    }

    Predicate<ButtonEventDto> sameAsClick(ButtonActionDef click) {
        return q -> equalsTrigger(click).test(q.getTrigger());
    }

    Predicate<String> equalsTrigger(ButtonActionDef click) {
        return click.getTrigger()::equals;
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
