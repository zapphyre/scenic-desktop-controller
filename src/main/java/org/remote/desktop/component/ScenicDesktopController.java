package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.facade.TimedButtonGamepadFactory;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.model.XdoActionVto;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static jxdotool.xDoToolUtil.*;

@Component
@RequiredArgsConstructor
public class ScenicDesktopController {

    private final TimedButtonGamepadFactory timedButtonGamepadFactory;
    private final ButtonPressMapper buttonPressMapper;
    private final GPadEventStreamService gPadEventStreamService;
    private SceneVto forcedScene;

    @PostConstruct
    void employController() {
        Flux.merge(timedButtonGamepadFactory.getButtonStream(), timedButtonGamepadFactory.getArrowsStream())
                .map(buttonPressMapper::map)
                .flatMap(getNextSceneXdoAction)
                .doOnNext(q -> {
                    if (forcedScene == null)
                        forcedScene = q.nextScene();
                })
                .map(NextSceneXdoAction::actions)
                .subscribe(act);
    }

    Function<ButtonActionDef, Mono<NextSceneXdoAction>> getNextSceneXdoAction = q -> isSceneForced() ?
            getActionsOn(GPadEventStreamService::relativeWindowNameActions, getCurrentWindowTitle(), q) :
            getActionsOn(GPadEventStreamService::extractInheritedActions, forcedScene, q);

    boolean isSceneForced() {
        return forcedScene == null;
    }

    <P> Mono<NextSceneXdoAction> getActionsOn(BiFunction<GPadEventStreamService, P, Map<ButtonActionDef, NextSceneXdoAction>> paramGetter, P param, ButtonActionDef buttons) {
        return Mono.justOrEmpty(paramGetter.apply(gPadEventStreamService, param))
                .mapNotNull(getActionsForButtons(buttons));
    }

    Function<Map<ButtonActionDef, NextSceneXdoAction>, NextSceneXdoAction> getActionsForButtons(ButtonActionDef def) {
        return q -> q.get(def);
    }

    Consumer<Set<XdoActionVto>> act = q -> q.forEach(p -> {
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
