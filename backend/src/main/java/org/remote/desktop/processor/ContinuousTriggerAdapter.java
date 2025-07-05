package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.GamepadEvent;
import org.asmus.model.NamingConstants;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.service.impl.GPadEventStreamService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;
import static org.remote.desktop.util.ETriggerFilter.triggerByOf;

@Component
public class ContinuousTriggerAdapter extends ButtonProcessorBase {

    public ContinuousTriggerAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher,
                                    GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver,
                                    TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executor, SettingsDao settingsDao) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executor, settingsDao);
    }

    public Consumer<Map<String, Integer>> getLeftContinuousTriggerProcessor() {
        return gamepadObserver.leftTriggerContinuousProcessor()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getLeftStepTriggerProcessor() {
        return gamepadObserver.leftDigitizedRangeTriggerStream()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getRightContinuousTriggerProcessor() {
        return gamepadObserver.rightTriggerContinuousProcessor()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getRightStepTriggerProcessor() {
        return gamepadObserver.rightDigitizedRangeTriggerStream()::processArrowEvents;
    }

    @Override
    protected Predicate<ButtonActionDef> purgingFilter() {
        return not(nullLogicalEvent).and(stepUpOrDown);
    }

    Predicate<ButtonActionDef> nullLogicalEvent = q -> Objects.isNull(q.getLogicalEventType());
    Predicate<ButtonActionDef> stepUpOrDown = q ->
            q.getLogicalEventType().equals(ELogicalEventType.STEP_NEGATIVE) ||
                    q.getLogicalEventType().equals(ELogicalEventType.STEP_POSITIVE);


    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerByOf(EButtonAxisMapping.TRIGGER_LEFT);
    }
}
