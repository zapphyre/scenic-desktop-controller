package org.remote.desktop.service;

import com.google.common.base.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EQualificationType;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.vto.GPadEventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.remote.desktop.ui.view.component.SceneUi.scrapeActionsRecursive;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GPadEventStreamService {

    private final SceneDao sceneDao;
    private final ButtonPressMapper buttonPressMapper;
    private final SceneStateRepository sceneStateRepository;
    private final Set<ButtonActionDef> appliedCommands = new HashSet<>();

    private final CacheManager cacheManager;

    private final String SERVICE_CACHE = "gpadEventStreamService";

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
        return Optional.ofNullable(windowName)
                .map(sceneDao::getSceneForWindowNameOrBase)
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> extractInheritedActions(SceneVto sceneVto) {
        return getRecursiveScrapedGpadEvents(sceneVto).stream()
                .map(buttonPressMapper.map(sceneVto.getWindowName()))
                .collect(toMap(SceneBtnActions::action, o -> NextSceneXdoAction.builder()
                        .actions(o.actions)
                        .nextScene(o.nextScene)
                        .build(), (p, q) -> q));
    }

    public Predicate<GPadEventVto> triggerAndModifiersSameAsClick(ButtonActionDef click) {
        return q -> sameAsClick(click).test(q.getTrigger()) ||
                q.getModifiers().stream().anyMatch(sameAsClick(click));
    }

    public Predicate<EButtonAxisMapping> sameAsClick(ButtonActionDef click) {
        return q -> q == click.getTrigger();
    }

    @Cacheable(SERVICE_CACHE)
    public List<GPadEventVto> getRecursiveScrapedGpadEvents(SceneVto scene) {
        return Stream.of(scrapeActionsRecursive(scene), scene.getGPadEvents())
                .flatMap(Collection::stream)
                .toList();
    }

    public boolean getActuatorForScene(ButtonActionDef click) {
        if (click.getQualified() == EQualificationType.ARROW)
            return true;

        SceneVto scene = sceneStateRepository.isSceneForced() ?
                sceneStateRepository.getForcedScene() : sceneDao.getSceneForWindowNameOrBase(sceneStateRepository.tryGetCurrentName());

        EQualifiedSceneDict foundQualifier = Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> getRecursiveScrapedGpadEvents(scene).stream()
                        .filter(triggerAndModifiersSameAsClick(click))
                        .anyMatch(q.getPredicate())
                )
                .findFirst()
                .orElse(EQualifiedSceneDict.FAST_CLICK);

        return foundQualifier.getQualifierType().ordinal() == click.getQualified().ordinal();
    }

    public boolean addAppliedCommand(ButtonActionDef click) {
        List<ButtonActionDef> defs = Arrays.stream(EQualificationType.values())
                .filter(q -> q.ordinal() > click.getQualified().ordinal())
                .map(q -> ButtonActionDef.builder()
                        .trigger(click.getTrigger())
                        .modifiers(click.getModifiers())
                        .longPress(click.isLongPress())
                        .qualified(q)
                        .build())
                .toList();

        boolean b = appliedCommands.addAll(defs);
        return true;
    }

    public boolean withoutPreviousRelease(ButtonActionDef def) {
        return !appliedCommands.remove(def);
    }

    public record SceneBtnActions(String windowName, ActionMatch action, List<XdoActionVto> actions,
                                  SceneVto nextScene) {
    }
}
