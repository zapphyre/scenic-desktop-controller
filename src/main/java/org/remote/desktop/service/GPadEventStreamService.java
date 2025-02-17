package org.remote.desktop.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.component.SceneDbToolbox;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.model.XdoActionVto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.remote.desktop.ui.view.component.SceneUi.scrapeActionsRecursive;


@Service
@RequiredArgsConstructor
public class GPadEventStreamService {

    private final SceneDbToolbox sceneDbToolbox;

    @Cacheable(SceneDbToolbox.WINDOW_SCENE_CACHE_NAME)
    public Map<ButtonActionDef, NextSceneXdoAction> relativeWindowNameActions(String windowName) {
        return Optional.of(sceneDbToolbox.getAllScenes().stream()
                        .filter(q -> !Strings.isNullOrEmpty(q.getWindowName()))
                        .filter(q -> windowName.contains(q.getWindowName()))
                        .findFirst().orElseGet(() -> sceneDbToolbox.getScene("Base"))
                )
                .map(this::extractInheritedActions)
                .orElse(Map.of());
    }

    @Cacheable(SceneDbToolbox.SCENE_NAME_CACHE_NAME)
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

    record SceneBtnActions(String name, ButtonActionDef buttonActionDef, Set<XdoActionVto> actions,
                           SceneVto nextScene) {
    }
}
