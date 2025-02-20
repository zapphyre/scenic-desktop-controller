package org.remote.desktop.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.asmus.model.BehavioralFilter;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.component.SceneDao;
import org.remote.desktop.model.*;
import org.remote.desktop.pojo.EQualifiedSceneDict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.remote.desktop.ui.view.component.SceneUi.scrapeActionsRecursive;


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

    public BehavioralFilter getActuatorForScene(String currentWindowTitle) {
        SceneVto scene = sceneDao.getSceneLikeName(currentWindowTitle);

        EQualifiedSceneDict foundQualifier = Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> scene.getGPadEvents().stream()
                        .anyMatch(q.getPredicate()))
                .findFirst()
                .orElse(EQualifiedSceneDict.FAST_CLICK);

        List<EButtonAxisMapping> buttons = scene.getGPadEvents().stream()
                .map(GPadEventVto::getTrigger)
                .toList();

        return BehavioralFilter.builder()
                .behaviour(foundQualifier.getBehaviour())
                .buttons(buttons)
                .build();
    }

    record SceneBtnActions(String name, ButtonActionDef buttonActionDef, Set<XdoActionVto> actions,
                           SceneVto nextScene) {
    }
}
