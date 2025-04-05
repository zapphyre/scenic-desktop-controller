package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TimedValue;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.remote.desktop.util.EtriggerFilter.triggerUpTo;

@Component
public class ButtonAdapter extends ButtonProcessorBase {

    Predicate<ButtonActionDef> consumeLeftovers = gPadEventStreamService::consumedEventLeftovers;
//    Predicate<ButtonActionDef> relevantQualification = gPadEventStreamService::isCurrentClickQualificationSceneRelevant;
    Predicate<ButtonActionDef> appliedCommand = gPadEventStreamService::addAppliedCommand;

    public ButtonAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher);
    }

    public Consumer<List<TimedValue>> getButtonConsumer() {
        return gamepadObserver.getButtonStream()::processButtonEvents;
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerUpTo(EButtonAxisMapping.OTHER);
    }

    @Override
    public Predicate<ButtonActionDef> purgingFilter() {
        return consumeLeftovers;
    }

    @Override
    public Predicate<ButtonActionDef> notingFilter() {
        return appliedCommand;
    }
}
