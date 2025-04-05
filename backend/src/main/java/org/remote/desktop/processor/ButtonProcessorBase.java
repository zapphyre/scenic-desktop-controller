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
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

@RequiredArgsConstructor
public abstract class ButtonProcessorBase {

    protected final ButtonPressMapper buttonPressMapper;
    protected final ApplicationEventPublisher eventPublisher;
    protected final GPadEventStreamService gPadEventStreamService;
    protected final IntrospectedEventFactory gamepadObserver;
    protected final TriggerActionMatcher triggerActionMatcher;
    private final ScheduledExecutorService executorService;

    protected abstract Predicate<GamepadEvent> triggerFilter();

    @PostConstruct
    void process() {
        gamepadObserver.getButtonEventStream()
                .filter(triggerFilter())
                .map(buttonPressMapper::map)
                .filter(purgingFilter())
                .doOnNext(this::qualificationExamine)
                .flatMap(triggerActionMatcher.actionPickPipeline)
                .publishOn(Schedulers.fromExecutorService(executorService))
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }

    public Predicate<ButtonActionDef> purgingFilter() {
        return q -> true;
    }

    public void qualificationExamine(ButtonActionDef click) {
    }
}
