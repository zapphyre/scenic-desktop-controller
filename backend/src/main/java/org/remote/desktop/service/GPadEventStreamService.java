package org.remote.desktop.service;

import jakarta.annotation.PostConstruct;
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
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.remote.desktop.util.RecursiveScraper;
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
    private final RecursiveScraper<GamepadEventDto, SceneDto> scraper = new RecursiveScraper<>();

    private boolean sceneSet;

    @PostConstruct
    void init() {
        sceneStateRepository.registerForcedSceneObserver(q -> {
            sceneSet = true;
            System.out.println("setting scene forced");
        });
    }

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

    SceneDto workingOn;

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> extractInheritedActions(SceneDto sceneDto) {
        return scraper.scrapeActionsRecursive(sceneDto).stream()
                .map(buttonPressMapper.map(sceneDto.getWindowName()))
                .collect(toMap(SceneBtnActions::action, buttonPressMapper::map, (p, q) -> q));
    }

    Set<EQualificationType> qualificationReceived = new HashSet<>();

    public boolean isCurrentClickQualificationSceneRelevant(ButtonActionDef click) {
//        if (sceneSet && click.getQualified() != EQualificationType.MULTIPLE) {
//            System.out.print          ln("discarding click: " + click);
//            return false;
//        } else {
//            sceneSet = false;
//            System.out.println("falsified sceneSet, continue            ");
//        }

        SceneDto scene = sceneStateRepository.isSceneForced() ?
                sceneStateRepository.getForcedScene() :
                sceneDao.getSceneForWindowNameOrBase(sceneStateRepository.tryGetCurrentName());

        return sceneClickQualificationRelevant(click, scene);
    }

    public boolean sceneClickQualificationRelevant(ButtonActionDef click, SceneDto scene) {
        System.out.println("scene relevancy for: " + scene.getName());

        EQualifiedSceneDict foundQualifier = Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> scraper.scrapeActionsRecursive(scene).stream()
                        .filter(triggerAndModifiersSameAsClick(click))
                        .anyMatch(q.getPredicate()))
                .findFirst()
                .orElse(EQualifiedSceneDict.FAST_CLICK);


        boolean b = foundQualifier.getQualifierType() == click.getQualified();

        if (b) {
            System.out.println("lowest scene qualificator was: " + foundQualifier.getQualifierType());
            System.out.println("click qualifier was: " + click.getQualified());

            System.out.println("letting pass: " + click);
        }

        return b;

//        return click.withLongestSceneQualification(foundQualifier.getQualifierType())
//                .withCreatedForScene(scene.getName());
    }

    Set<EQualificationType> issued = new HashSet<>();

    public boolean addAppliedCommand(ButtonActionDef click) {
        if (click.getQualified() == EQualificationType.PUSH) {
            boolean b = qualificationReceived.addAll(Arrays.asList(
                    EQualificationType.RELEASE,
                    EQualificationType.LONG,
                    EQualificationType.MULTIPLE
            ));

            if (!b)
                System.out.println("couldn't add on PUSH");
        }

        if (click.getQualified() == EQualificationType.LONG &&
                !qualificationReceived.contains(EQualificationType.RELEASE)) {

            boolean b = qualificationReceived.add(EQualificationType.RELEASE);

            if (!b)
                System.out.println("couldn't add on LONG");
        }

        if (click.getQualified() == EQualificationType.RELEASE &&
                !qualificationReceived.contains(EQualificationType.LONG)) {

            boolean b = qualificationReceived.add(EQualificationType.LONG);

            if (!b)
                System.out.println("couldn't add on RELEASE ");
        }

        if (click.getQualified() != EQualificationType.MULTIPLE)
            qualificationReceived.add(EQualificationType.MULTIPLE);

        System.out.println("passed: " + click.getQualified());
        System.out.println("WILL TRIGGER: " + click);
        System.out.println("qualificationReceived: " + qualificationReceived);

        return true;
    }

//    public boolean addAppliedCommand(ButtonActionDef click) {
//        if (click.getQualified() == EQualificationType.MULTIPLE) {
//            System.out.println("letting pass multiple qualified click");
//            return true;
//        }
//
//        boolean canAdd = true;
//
//        if (click.getQualified() == EQualificationType.RELEASE) {
//            System.out.println("adding long");
//            canAdd &= appliedCommands.add(click.withQualified(EQualificationType.LONG));
//        }
//
//        if (click.getQualified() == EQualificationType.LONG) {
//            System.out.println("adding release");
//            System.out.println("applied commands are: " + appliedCommands);
//            canAdd = appliedCommands.add(click.withQualified(EQualificationType.RELEASE));
//        }
//
//
//        List<ButtonActionDef> defs = List.of();
//        if (click.getQualified() == EQualificationType.PUSH) {
//
//            defs = Arrays.stream(EQualificationType.values())
//                    .filter(q -> q.ordinal() > click.getQualified().ordinal())
////                .filter(q -> q.ordinal() > click.getLongestSceneQualification().ordinal())
//                    .map(click::withQualified)
//                    .toList();
//
//            System.out.println("will be adding: " + defs);
//            canAdd &= appliedCommands.addAll(defs);
//
//        } else
//            canAdd &= appliedCommands.add(click.withQualified(EQualificationType.MULTIPLE));
//
//
////
//
//        if (!canAdd)
//            System.out.println("COULDN'T ADD. THIS SHOULD NOT HAPPENED");
//
////
//        System.out.println("with result: " + canAdd);
//

    /// /        if (click.getQualified().ordinal() <= click.getLongestSceneQualification().ordinal()) {
    /// /            System.out.println("letting pass click without creating blockers: " + click);
    /// /            return true;
    /// /        }
//
//        System.out.println("============ end of event ============");
//        return canAdd;
//    }
    public boolean consumedEventLeftovers(ButtonActionDef def) {
        System.out.println("will be consumed: " + def.getQualified());
        System.out.println("from: " + qualificationReceived);
//        issued.remove(def.getQualified());
        boolean b = !qualificationReceived.remove(def.getQualified());

        if (def.getQualified() == EQualificationType.MULTIPLE) {
            qualificationReceived.clear();
        }

        System.out.println("will go next: " + b);
        return b;
    }
//    public boolean consumedEventLeftovers(ButtonActionDef def) {
//        System.out.println("trying to remove: " + def);
//        System.out.println("from: " + appliedCommands);
//
//        boolean b = !appliedCommands.remove(def);
//
//        System.out.println("is it going next: " + b);
//        System.out.println("appliedCommands left: " + appliedCommands);
//
//        System.out.println("=======================================");
//        return b;
//    }

    public record SceneLongestQualification(EQualificationType longest, ButtonActionDef click) {
    }

    public record SceneBtnActions(String windowName, ActionMatch action, List<XdoActionDto> actions,
                                  SceneDto nextScene) {
    }
}
