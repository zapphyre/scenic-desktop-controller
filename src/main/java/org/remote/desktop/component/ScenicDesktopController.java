package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.builder.closure.OsDevice;
import org.asmus.builder.closure.RawArrowSource;
import org.asmus.service.JoyWorker;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.model.XdoActionVto;
import org.remote.desktop.repository.GPadEventRepository;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static jxdotool.xDoToolUtil.*;

@Component
@RequiredArgsConstructor
public class ScenicDesktopController {

    private final JoyWorker worker;
    private final ButtonPressMapper buttonPressMapper;
    private final GPadEventStreamService gPadEventStreamService;
    private final GPadEventRepository eventRepository;
    private SceneVto forcedScene;
    private Disposable currentSubs;

    @PostConstruct
    void employController() {
        IntrospectedEventFactory gamepadObserver = new IntrospectedEventFactory();

        OsDevice wrapper = gamepadObserver.getButtonStream().act(gPadEventStreamService::getActuatorForScene);
        RawArrowSource arrowSource = gamepadObserver.getArrowsStream();

        worker.getButtonStream().subscribe(wrapper::processButtonEvents);
        worker.getAxisStream().subscribe(arrowSource::processArrowEvents);

        gamepadObserver.getEventStream()
                .map(buttonPressMapper::map)
                .flatMap(getNextSceneXdoAction)
                .doOnNext(q -> {
                    if (forcedScene == null)
                        forcedScene = q.nextScene();
                })
                .map(NextSceneXdoAction::actions)
                .log()
                .subscribe(act, q -> System.out.println(q.getMessage()));
    }

    Function<ButtonActionDef, Mono<NextSceneXdoAction>> getNextSceneXdoAction = q -> isSceneForced() ?
            getActionsOn(GPadEventStreamService::relativeWindowNameActions, getCurrentWindowTitle(), q) :
            getActionsOn(GPadEventStreamService::extractInheritedActions, forcedScene, q);

    boolean isSceneForced() {
        return Objects.isNull(forcedScene);
    }

    <P> Mono<NextSceneXdoAction> getActionsOn(BiFunction<GPadEventStreamService, P, Map<ButtonActionDef, NextSceneXdoAction>> paramGetter,
                                              P param,
                                              ButtonActionDef buttons) {
        return Mono.justOrEmpty(paramGetter.apply(gPadEventStreamService, param))
                .mapNotNull(getActionsForButtons(buttons));
    }

    Function<Map<ButtonActionDef, NextSceneXdoAction>, NextSceneXdoAction> getActionsForButtons(ButtonActionDef def) {
        return q -> q.get(def);
    }

    Consumer<Set<XdoActionVto>> act = q ->
            q.stream()
                    .sorted((x, y) -> x.getId() > y.getId() ? -1 : 1)
                    .forEach(p -> {
                        System.out.println("executing: " + p.getKeyPress());
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
