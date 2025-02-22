package org.remote.desktop.service;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.model.ButtonClick;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.component.SceneDao;
import org.remote.desktop.model.*;
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static jxdotool.xDoToolUtil.getCurrentWindowTitle;
import static org.remote.desktop.ui.view.component.SceneUi.scrapeActionsRecursive;


@Slf4j
@Service
@RequiredArgsConstructor
public class GPadEventStreamService {

    private final SceneDao sceneDao;

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ButtonActionDef, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
        return Optional.ofNullable(sceneDao.getAllScenes().stream()
                        .filter(q -> !Strings.isNullOrEmpty(q.getWindowName()))
                        .filter(q -> windowName.contains(q.getWindowName()))
                        .findFirst().orElseGet(() -> sceneDao.getScene("Base"))
                )
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ButtonActionDef, NextSceneXdoAction> extractInheritedActions(SceneVto sceneVto) {
        return Stream.of(scrapeActionsRecursive(sceneVto), sceneVto.getGPadEvents())
                .flatMap(Collection::stream)
                .map(p -> new SceneBtnActions(sceneVto.getWindowName(), ButtonActionDef.builder()
                        .trigger(p.getTrigger())
                        .modifiers(p.getModifiers())
                        .longPress(p.isLongPress())
                        .build(), p.getActions(), p.getNextScene()))
                .collect(toMap(SceneBtnActions::buttonActionDef, o -> new NextSceneXdoAction(o.nextScene, o.actions), (p, q) -> q));
    }

    Predicate<EButtonAxisMapping> sameAsClick(ButtonClick click) {
        return q -> q == EButtonAxisMapping.getByName(click.getPush().getName());
    }

    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public ActuationBehaviour getActuatorForScene(ButtonClick click) {
        System.out.println("click: " + click);

        SceneVto scene = sceneDao.getSceneLikeName(getCurrentWindowTitle());
        List<GPadEventVto> gPadEventVtos = scrapeActionsRecursive(scene);

        EQualifiedSceneDict foundQualifier = Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> gPadEventVtos.stream()
                        .filter(p -> sameAsClick(click).test(p.getTrigger()) ||
                                p.getModifiers().stream().anyMatch(sameAsClick(click)))
                        .anyMatch(q.getPredicate()))
                .findFirst()
                .orElse(EQualifiedSceneDict.FAST_CLICK);

        System.out.println("found qualifier: " + foundQualifier.name());
            return foundQualifier.getBehaviour();

//        return EQualifiedSceneDict.MODIFIED.getBehaviour();
    }

    record SceneBtnActions(String name, ButtonActionDef buttonActionDef, List<XdoActionVto> actions,
                           SceneVto nextScene) {}
}
