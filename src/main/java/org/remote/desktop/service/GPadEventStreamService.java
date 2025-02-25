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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.remote.desktop.ui.view.component.SceneUi.scrapeActionsRecursive;


@Slf4j
@Service
@RequiredArgsConstructor
public class GPadEventStreamService {

    private final SceneDao sceneDao;
    private final ButtonPressMapper buttonPressMapper;
    private final SceneStateRepository sceneStateRepository;

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
//        System.out.println("getting relative window name" + windowName);
        return Optional.ofNullable(windowName)
                .map(sceneDao::getSceneForWindowNameOrBase)
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

//    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ActionMatch, NextSceneXdoAction> extractInheritedActions(SceneVto sceneVto) {
//        System.out.println("getting mappings for: " + sceneVto.getName());

        Map<ActionMatch, NextSceneXdoAction> collect = Stream.of(scrapeActionsRecursive(sceneVto), sceneVto.getGPadEvents())
                .flatMap(Collection::stream)
//        Map<ActionMatch, NextSceneXdoAction> collect = scrapeActionsRecursive(sceneVto).stream()
                .map(buttonPressMapper.map(sceneVto.getWindowName()))
                .collect(toMap(SceneBtnActions::action, o -> NextSceneXdoAction.builder()
                        .actions(o.actions)
                        .nextScene(o.nextScene)
                        .build(), (p, q) -> q));

        collect.forEach((key, value) -> System.out.println(key + ": " + value));

        return collect;
    }

    public Predicate<GPadEventVto> triggerAndModifiersSameAsClick(GamepadEvent click) {
        return q -> sameAsClick(click).test(q.getTrigger()) ||
                q.getModifiers().stream().anyMatch(sameAsClick(click));
    }

    public Predicate<EButtonAxisMapping> sameAsClick(GamepadEvent click) {
        return q -> q == click.getType();
    }

    Set<ButtonActionDef> appliedCommands = new HashSet<>();

//    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public boolean getActuatorForScene(GamepadEvent click) {
//        if (releaseOfPrevious(click)) {
//            return false;
//        }
        if (click.getQualified() == EQualificationType.ARROW)
            return true;

        SceneVto scene = sceneStateRepository.isSceneForced() ?
                sceneStateRepository.getForcedScene() : sceneDao.getSceneForWindowNameOrBase(sceneStateRepository.tryGetCurrentName());

        System.out.println("getActuatorForScene: " + scene);

        List<GPadEventVto> gPadEventVtos = scrapeActionsRecursive(scene);

        EQualifiedSceneDict foundQualifier = Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> gPadEventVtos.stream()
                        .filter(triggerAndModifiersSameAsClick(click))
                        .anyMatch(q.getPredicate())
                )
                .findFirst()
                .orElseGet(() -> {
//                    System.out.println("was not found");
                    return EQualifiedSceneDict.FAST_CLICK;
                });

        System.out.println("foundQualifier: " + foundQualifier);

//        if (foundQualifier.getQualifierType() == click.getQualified()) {
//            boolean add = appliedCommands.add(buttonPressMapper.map(click));
//            System.out.println("adding to command buffer result: " + add);
//
//            return add;
//        }
//
//        return false;
        boolean b = foundQualifier.getQualifierType().ordinal() == click.getQualified().ordinal();


        return b;
    }

    public boolean addAppliedCommand(ButtonActionDef click) {
//        appliedCommands.clear();

        System.out.println("before reduce");
        showAppliedCommands();

        List<ButtonActionDef> list = Arrays.stream(EQualificationType.values())
                .filter(q -> q.ordinal() > click.getQualified().ordinal())
                .map(q -> ButtonActionDef.builder()
                        .trigger(click.getTrigger())
                        .modifiers(click.getModifiers())
                        .longPress(click.isLongPress())
                        .qualified(q)
                        .build())
                .toList();

        appliedCommands.addAll(list);
//                .reduce(true, (p, q) -> p & appliedCommands.add(click), (a, b) -> a && b);

//        System.out.println("reduced: " + reduce);

        System.out.println("after reduce");
        showAppliedCommands();

//        return reduce;

        return true;

//        boolean add = appliedCommands.add(click);
//        System.out.println("addAppliedCommand: " + appliedCommands);
//        return add;
    }

    void showAppliedCommands() {
        System.out.println("appliedCommands: ");
        appliedCommands.forEach(System.out::println);
    }

    public boolean withoutPreviousRelease(GamepadEvent click) {
//        if (click.getQualified().ordinal() > 1) {
//
//        }

        System.out.println("applied commands before remove");
        showAppliedCommands();

        ButtonActionDef def = buttonPressMapper.map(click);
        boolean remove = appliedCommands.remove(def);

        System.out.println("removing click: " + click);
        System.out.println("result: " + remove);

            return !remove;
    }

    public record SceneBtnActions(String windowName, ActionMatch action, List<XdoActionVto> actions,
                                  SceneVto nextScene) {
    }
}
