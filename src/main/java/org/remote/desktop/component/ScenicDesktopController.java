package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.facade.TimedButtonGamepadFactory;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.model.XdoActionVto;
import org.remote.desktop.service.SceneService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static jxdotool.xDoToolUtil.*;

@Component
@RequiredArgsConstructor
public class ScenicDesktopController {

    private final static TimedButtonGamepadFactory timedButtonGamepadFactory = new TimedButtonGamepadFactory();
    private final static List<Runnable> factoryDisposable = timedButtonGamepadFactory.watchForDevices(0, 1);

    private final SceneService sceneService;
    private SceneVto forcedScene;

    @PostConstruct
    public void employController() {
        Flux.merge(timedButtonGamepadFactory.getButtonStream(), timedButtonGamepadFactory.getArrowsStream())
                .log()
                .map(q -> ButtonActionDef.builder()
                        .trigger(q.getType())
                        .modifiers(q.getModifiers())
                        .build())
                .flatMap(forcedScene == null ?
                        getActionsOn(SceneService::relativeWindowNameActions, getCurrentWindowTitle()) :
                        getActionsOn(SceneService::extractActions, forcedScene)
                )
                .map(q -> {
                    if (forcedScene == null)
                        forcedScene = q.nextScene();

                    return q.actions();
                })
                .subscribe(act);
    }

    <P> Function<ButtonActionDef, Mono<NextSceneXdoAction>> getActionsOn(BiFunction<SceneService, P, Map<ButtonActionDef, NextSceneXdoAction>> paramGetter, P param) {
        return q -> Mono.justOrEmpty(paramGetter.apply(sceneService, param))
                .mapNotNull(getActionsForButtons(q));
    }

    Function<Map<ButtonActionDef, NextSceneXdoAction>, NextSceneXdoAction> getActionsForButtons(ButtonActionDef def) {
        return q -> q.get(def);
    }

    Consumer<List<XdoActionVto>> act = q -> q.forEach(p -> {
        switch (p.getKeyEvt()) {
            case PRESS:
                keydown(p.getKeyPress());
                break;
            case STROKE:
                pressKey(p.getKeyPress());
                break;
            case RELEASE:
                keyup(p.getKeyPress());
                break;
            case SCENE_RESET:
                forcedScene = null;
                break;
            case TIMEOUT:
                try {
                    Thread.sleep(Integer.parseInt(p.getKeyPress()));
                } catch (InterruptedException e) {
                }
                break;
        }
    });
}
