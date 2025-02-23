package org.remote.desktop.component;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.builder.closure.button.OsDevice;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.service.JoyWorker;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static jxdotool.xDoToolUtil.getCurrentWindowTitle;

@Component
@RequiredArgsConstructor
public class ButtonAdapter {

    private final JoyWorker worker;
    private final ButtonPressMapper buttonPressMapper;
    private final GPadEventStreamService gPadEventStreamService;
    private final ApplicationEventPublisher eventPublisher;
    private final SceneStateRepository actuatedStateRepository;
    private final SceneStateRepository sceneStateRepository;

    @PostConstruct
    void employController() {
        IntrospectedEventFactory gamepadObserver = new IntrospectedEventFactory();

        OsDevice wrapper = gamepadObserver.getButtonStream().act(gPadEventStreamService::getActuatorForScene);
        RawArrowSource arrowSource = gamepadObserver.getArrowsStream();

        worker.getButtonStream().subscribe(wrapper::processButtonEvents);
        worker.getAxisStream().subscribe(arrowSource::processArrowEvents);

        gamepadObserver.getEventStream()
                .map(buttonPressMapper::map)
                .flatMap(this::getNextSceneButtonEvent)
                .flatMap(q -> Flux.fromIterable(q.actions())
                        .map(x -> new XdoCommandEvent(this, x.getKeyEvt(), x.getKeyPress(), q.nextScene()))
                )
                .subscribe(eventPublisher::publishEvent);
    }

    Mono<NextSceneXdoAction> getNextSceneButtonEvent(ButtonActionDef q) {
        return actuatedStateRepository.isSceneForced() ?
                getActionsOn(GPadEventStreamService::relativeWindowNameActions, sceneStateRepository.tryGetCurrentName(), q) :
                getActionsOn(GPadEventStreamService::extractInheritedActions, actuatedStateRepository.getForcedScene(), q);
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
}
