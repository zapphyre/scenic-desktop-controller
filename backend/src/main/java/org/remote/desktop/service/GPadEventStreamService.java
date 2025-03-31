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
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.remote.desktop.util.RecursiveScraper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;
import static jxdotool.xDoToolUtil.runIdentityScript;

@Slf4j
@Service
@RequiredArgsConstructor
public class GPadEventStreamService {

    private final SceneDao sceneDao;
    private final ButtonPressMapper buttonPressMapper;
    private final SceneStateRepository sceneStateRepository;
    private final Set<ButtonActionDef> appliedCommands = new HashSet<>();
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
                .map(buttonPressMapper.map(sceneDto.getWindowName()))
                .collect(toMap(SceneBtnActions::action, buttonPressMapper::map, (p, q) -> q));
    }

    public boolean isCurrentClickQualificationSceneRelevant(ButtonActionDef click) {
        SceneDto scene = sceneStateRepository.isSceneForced() ?
                sceneStateRepository.getForcedScene() :
                sceneDao.getSceneForWindowNameOrBase(sceneStateRepository.tryGetCurrentName());

        return sceneClickQualificationRelevant(click, scene);
    }

    public boolean sceneClickQualificationRelevant(ButtonActionDef click, SceneDto scene) {
        EQualifiedSceneDict foundQualifier = Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> scraper.scrapeActionsRecursive(scene).stream()
                        .filter(triggerAndModifiersSameAsClick(click))
                        .anyMatch(q.getPredicate()))
                .findFirst()
                .orElse(EQualifiedSceneDict.FAST_CLICK);

        return foundQualifier.getQualifierType() == click.getQualified();
    }

    public boolean addAppliedCommand(ButtonActionDef click) {
        List<ButtonActionDef> defs = Arrays.stream(EQualificationType.values())
                .filter(q -> q.ordinal() > click.getQualified().ordinal())
                .filter(q -> q != EQualificationType.ARROW)
//                .filter(q -> q != EQualificationType.MULTIPLE)
                .map(click::withQualified)
                .toList();

        for (ButtonActionDef def : defs) {
            if (def.getQualified() == EQualificationType.MULTIPLE) {
                System.out.println("Mutiple added");
            }

        }


        boolean b = appliedCommands.addAll(defs);
        System.out.println("should be adding: " + defs + " for click q: " + click.getQualified());
        System.out.println("appliedCommands: " + appliedCommands);
        System.out.println("result: " + b);

        return b;
//
//        return true; //forced b/c of arrows
    }

    public boolean consumedEventLeftovers(ButtonActionDef def) {
        System.out.println("trying to remove: " + def);
        System.out.println("from: " + appliedCommands);

        boolean b = !appliedCommands.remove(def);

        System.out.println("result: " + b);
        System.out.println("appliedCommands left: " + appliedCommands);

        System.out.println("=======================================");
        return b;
    }

    public record SceneBtnActions(String windowName, ActionMatch action, List<XdoActionDto> actions,
                                  SceneDto nextScene) {
    }
}
