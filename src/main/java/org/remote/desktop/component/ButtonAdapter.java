package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TimedValue;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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
    private final Sinks.Many<GamepadEvent> arrowSink = Sinks.many().multicast().directBestEffort();

    @PostConstruct
    void employController() {
        gamepadObserver.getButtonEventStream()
                .log()
                .map(buttonPressMapper::map)
                .filter(gPadEventStreamService::withoutPreviousRelease)
                .filter(gPadEventStreamService::getActuatorForScene)
                .flatMap(this::getNextSceneButtonEvent)
                .filter(q -> gPadEventStreamService.addAppliedCommand(q.getButtonTrigger()))
                .flatMap(q -> Flux.fromIterable(q.getActions())
                        .map(x -> new XdoCommandEvent(this, x.getKeyEvt(), x.getKeyPress(), q.getNextScene()))
                )
//                .publishOn(Schedulers.parallel())
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }

    public Consumer<List<TimedValue>> getButtonConsumer() {
        return gamepadObserver.getButtonStream()::processButtonEvents;
    }

    public Consumer<Map<String, Integer>> getArrowConsumer() {
        return gamepadObserver.getArrowsStream()::processArrowEvents;
    }

    Mono<NextSceneXdoAction> getNextSceneButtonEvent(ButtonActionDef q) {
//        System.out.println("getNextSceneButtonEvent; buttonDef: " + q);
//        System.out.println("isForced: " + actuatedStateRepository.isSceneForced());

        return actuatedStateRepository.isSceneForced() ?
                getActionsOn(GPadEventStreamService::extractInheritedActions, actuatedStateRepository.getForcedScene(), q) :
                getActionsOn(GPadEventStreamService::relativeWindowNameActions, sceneStateRepository.tryGetCurrentName(), q);
    }

    <P> Mono<NextSceneXdoAction> getActionsOn(BiFunction<GPadEventStreamService, P, Map<ActionMatch, NextSceneXdoAction>> paramGetter,
                                              P param,
                                              ButtonActionDef buttons) {
        return Mono.justOrEmpty(paramGetter.apply(gPadEventStreamService, param))
                .mapNotNull(getActionsForButtons(buttonPressMapper.map(buttons)))
                .map(q -> q.withButtonTrigger(buttons));
    }

    Function<Map<ActionMatch, NextSceneXdoAction>, NextSceneXdoAction> getActionsForButtons(ActionMatch def) {
        return q -> {
//            System.out.println("for buttonDef: " + def);

            NextSceneXdoAction nextSceneXdoAction = q.get(def);
            System.out.println("nextSceneXdoAction: " + nextSceneXdoAction);
            return nextSceneXdoAction;
        };
    }
}
