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
import org.remote.desktop.model.ELogicalTrigger;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.service.impl.GPadEventStreamService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public Consumer<Map<String, Integer>> getRightContinuousTriggerProcessor() {
        return gamepadObserver.rightTriggerContinuousProcessor()::processArrowEvents;
    }

    @Override
    protected Predicate<ButtonActionDef> purgingFilter() {
        return q -> {
            return q.getTrigger().equals("RIGHTTRIGGER_STEP_POSITIVE") ||
                    q.getTrigger().equals("LEFTTRIGGER_STEP_POSITIVE");
//            return q.getLogicalEventType() != null &&
//                    (q.getLogicalEventType().equals(ELogicalEventType.STEP_NEGATIVE) ||
//                            q.getLogicalEventType().equals(ELogicalEventType.STEP_POSITIVE));
        };
    }

    Function<ButtonActionDef, List<ButtonActionDef>> ease() {
        final AtomicInteger emissionCounter = new AtomicInteger(0);
        return button -> {
            // Normalize position to [0, 1] range (0 to 64,000 -> 0 to 1)
            double normalizedPosition = button.getPosition() / NamingConstants.MAX;

            // Calculate a dynamic frequency factor based on position
            // At low positions (e.g., 0-16,000), frequency starts high (e.g., 4)
            // At mid positions (e.g., 32,000), frequency reduces (e.g., 2-3)
            // At high positions (e.g., 64,000), frequency approaches 1
            int baseFrequency = (int) Math.max(1, 10 - (2 * normalizedPosition)); // Starts at 4, decreases to 1

            // Use AtomicInteger to track and increment counter
            int currentCount = emissionCounter.getAndIncrement();

            // Emit only if the counter aligns with the current frequency (e.g., every 4th, then 3rd, etc.)
            if (currentCount % baseFrequency != 0)
                return Collections.emptyList();

                // Apply quadratic ease-in for the number of copies at higher positions
                double easeFactor = Math.pow(normalizedPosition, 1);
                int emissionCount = (int) (easeFactor * 2); // Max 10 copies at high end
//                emissionCount = Math.max(1, emissionCount); // Ensure at least 1 emission

                // Return a list with the specified number of copies
                return IntStream.range(0, emissionCount)
                        .mapToObj(i -> button.toBuilder().build())
                        .collect(Collectors.toList());
        };
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerByOf(EButtonAxisMapping.TRIGGER_LEFT);
    }
}
