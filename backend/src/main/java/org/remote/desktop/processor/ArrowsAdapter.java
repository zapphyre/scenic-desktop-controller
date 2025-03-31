package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

import static org.remote.desktop.util.EtriggerFilter.triggerBetween;

@Component
public class ArrowsAdapter extends ButtonProcessorBase {


    public ArrowsAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher);
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerBetween(11, 15);
    }
}
