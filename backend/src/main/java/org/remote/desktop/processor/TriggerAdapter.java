package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.remote.desktop.util.ETriggerFilter.triggerByOf;

@Component
public class TriggerAdapter extends ButtonProcessorBase {

    public TriggerAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executor, SettingsDao settingsDao) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executor, settingsDao);
    }

    public Consumer<Map<String, Integer>> getLeftTriggerProcessor() {
        return gamepadObserver.leftTriggerStream()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getRightTriggerProcessor() {
        return gamepadObserver.rightTriggerStream()::processArrowEvents;
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerByOf(EButtonAxisMapping.TRIGGER_LEFT);
    }
}
