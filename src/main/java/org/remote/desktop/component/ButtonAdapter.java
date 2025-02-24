package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.builder.closure.button.OsDevice;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.model.TimedValue;
import org.asmus.service.JoyWorker;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ButtonAdapter {

    private final ButtonPressMapper buttonPressMapper;
    private final GPadEventStreamService gPadEventStreamService;
    private final ApplicationEventPublisher eventPublisher;
    private final SceneStateRepository actuatedStateRepository;
    private final SceneStateRepository sceneStateRepository;

    private final IntrospectedEventFactory gamepadObserver = new IntrospectedEventFactory();
    private final RawArrowSource arrowSource = gamepadObserver.getArrowsStream();

    @PostConstruct
    void employController() {
        gamepadObserver.getEventStream()
                .map(buttonPressMapper::map)
                .flatMap(this::getNextSceneButtonEvent)
                .flatMap(q -> Flux.fromIterable(q.actions())
                        .map(x -> new XdoCommandEvent(this, x.getKeyEvt(), x.getKeyPress(), q.nextScene()))
                )
                .subscribe(eventPublisher::publishEvent);
    }

    public Consumer<List<TimedValue>> getButtonConsumer() {
        return gamepadObserver.getButtonStream().act(gPadEventStreamService::getActuatorForScene)::processButtonEvents;
    }

    public Consumer<Map<String, Integer>> getArrowConsumer() {
        return arrowSource::processArrowEvents;
    }

    Mono<NextSceneXdoAction> getNextSceneButtonEvent(ButtonActionDef q) {
        return actuatedStateRepository.isSceneForced() ?
                getActionsOn(GPadEventStreamService::relativeWindowNameActions, sceneStateRepository.tryGetCurrentName(), q) :
                getActionsOn(GPadEventStreamService::extractInheritedActions, actuatedStateRepository.getForcedScene(), q);
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
}
