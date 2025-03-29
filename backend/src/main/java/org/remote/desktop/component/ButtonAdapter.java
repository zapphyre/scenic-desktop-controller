package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
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

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ButtonAdapter {

    private final ButtonPressMapper buttonPressMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final GPadEventStreamService gPadEventStreamService;
    private final IntrospectedEventFactory gamepadObserver;
    private final TriggerActionMatcher triggerActionMatcher;

    @PostConstruct
    void employController() {
        gamepadObserver.getButtonEventStream()
                .filter(q -> q.getType().ordinal() < 15)
                .map(buttonPressMapper::map)
                .filter(gPadEventStreamService::consumedEventLeftovers)
                .filter(gPadEventStreamService::isCurrentClickQualificationSceneRelevant)
                .filter(gPadEventStreamService::addAppliedCommand)
                .flatMap(triggerActionMatcher.actionPickPipeline)
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }

    public Consumer<List<TimedValue>> getButtonConsumer() {
        return gamepadObserver.getButtonStream()::processButtonEvents;
    }

    public Consumer<Map<String, Integer>> getArrowConsumer() {
        return gamepadObserver.getArrowsStream()::processArrowEvents;
    }
}
