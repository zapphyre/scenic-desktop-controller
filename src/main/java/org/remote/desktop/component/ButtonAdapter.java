package org.remote.desktop.component;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.builder.closure.button.OsDevice;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.service.JoyWorker;
import org.remote.desktop.event.ActuatedStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static jxdotool.xDoToolUtil.*;

@Component
@RequiredArgsConstructor
public class ButtonAdapter {

    private final JoyWorker worker;
    private final ButtonPressMapper buttonPressMapper;
    private final GPadEventStreamService gPadEventStreamService;
    private final ApplicationEventPublisher eventPublisher;
    private final ActuatedStateRepository actuatedStateRepository;
    private SceneVto forcedScene;

    @PostConstruct
    void employController() {
        IntrospectedEventFactory gamepadObserver = new IntrospectedEventFactory();

        OsDevice wrapper = gamepadObserver.getButtonStream().act(gPadEventStreamService::getActuatorForScene);
        RawArrowSource arrowSource = gamepadObserver.getArrowsStream();

        worker.getButtonStream().subscribe(wrapper::processButtonEvents);
        worker.getAxisStream().subscribe(arrowSource::processArrowEvents);

        gamepadObserver.getEventStream()
                .map(buttonPressMapper::map)
                .flatMap(this::getNextSceneXdoAction)
//                .doOnNext(q -> {
//                    if (forcedScene == null)
//                        forcedScene = q.nextScene();
//                })
                .flatMap(q -> Flux.fromIterable(q.actions())
                        .map(x -> new XdoCommandEvent(this, x.getKeyEvt(), x.getKeyPress(), q.nextScene()))
                )
                .subscribe(eventPublisher::publishEvent);
//                .subscribe(act, q -> System.out.println(q.getMessage()));
    }

    Mono<NextSceneXdoAction> getNextSceneXdoAction(ButtonActionDef q) {
        return actuatedStateRepository.isSceneForced() ?
                getActionsOn(GPadEventStreamService::relativeWindowNameActions, getCurrentWindowTitle(), q) :
                getActionsOn(GPadEventStreamService::extractInheritedActions, actuatedStateRepository.getForcedScene(), q);
    }

    boolean isSceneForced() {
        return Objects.isNull(forcedScene);
    }

    @WithSpan
    <P> Mono<NextSceneXdoAction> getActionsOn(BiFunction<GPadEventStreamService, P, Map<ButtonActionDef, NextSceneXdoAction>> paramGetter,
                                              P param,
                                              ButtonActionDef buttons) {
        return Mono.justOrEmpty(paramGetter.apply(gPadEventStreamService, param))
                .mapNotNull(getActionsForButtons(buttons));
    }

    @WithSpan
    Function<Map<ButtonActionDef, NextSceneXdoAction>, NextSceneXdoAction> getActionsForButtons(ButtonActionDef def) {
        return q -> q.get(def);
    }

    Consumer<List<XdoActionVto>> act = q ->
            q.forEach(p -> {
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
