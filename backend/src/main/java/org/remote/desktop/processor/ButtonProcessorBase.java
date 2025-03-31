package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Predicate;

@RequiredArgsConstructor
public abstract class ButtonProcessorBase {

    private final ButtonPressMapper buttonPressMapper;
    private final ApplicationEventPublisher eventPublisher;
    protected final GPadEventStreamService gPadEventStreamService;
    protected final IntrospectedEventFactory gamepadObserver;
    private final TriggerActionMatcher triggerActionMatcher;

    protected abstract Predicate<GamepadEvent> triggerFilter();

    @PostConstruct
    void process() {
        gamepadObserver.getButtonEventStream()
                .filter(triggerFilter())
                .map(buttonPressMapper::map)
                .filter(stateFiler())
                .flatMap(triggerActionMatcher.actionPickPipeline)
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }

    public Predicate<ButtonActionDef> stateFiler() {
        return q -> true;
    }
}
