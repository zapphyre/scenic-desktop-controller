package org.remote.desktop.service;

import com.google.common.base.Predicate;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.model.ButtonClick;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.db.dao.SceneDao;
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

    @WithSpan
    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public Map<ButtonActionDef, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
        return Optional.ofNullable(windowName)
                .map(sceneDao::getSceneForWindowNameOrBase)
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

    @WithSpan
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

    public Predicate<GPadEventVto> triggerAndModifiersSameAsClick(ButtonClick click) {
        return q -> sameAsClick(click).test(q.getTrigger()) ||
                q.getModifiers().stream().anyMatch(sameAsClick(click));
    }

    public Predicate<EButtonAxisMapping> sameAsClick(ButtonClick click) {
        return q -> q == EButtonAxisMapping.getByName(click.getPush().getName());
    }

    @WithSpan
    @Cacheable(SceneDao.WINDOW_SCENE_CACHE_NAME)
    public ActuationBehaviour getActuatorForScene(ButtonClick click) {
        String currentWindowTitle = getCurrentWindowTitle();
        SceneVto scene = sceneDao.getSceneForWindowNameOrBase(currentWindowTitle);
        List<GPadEventVto> gPadEventVtos = Stream.of(scene.getGPadEvents(), scrapeActionsRecursive(scene))
                .flatMap(Collection::stream)
                .toList();

        EQualifiedSceneDict foundQualifier = Arrays.stream(EQualifiedSceneDict.values())
                .filter(q -> gPadEventVtos.stream()
                        .filter(triggerAndModifiersSameAsClick(click))
                        .anyMatch(q.getPredicate())
                )
                .findFirst()
                .orElse(EQualifiedSceneDict.FAST_CLICK);

        return foundQualifier.getBehaviour();
    }

    record SceneBtnActions(String name, ButtonActionDef buttonActionDef, List<XdoActionVto> actions,
                           SceneVto nextScene) {
    }
}
