package org.remote.desktop.processor.keyboard;

import org.asmus.builder.IntrospectedEventFactory;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.event.VirtualInputStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.processor.ButtonProcessorBase;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

@Component
public abstract class KeyboardActionsBaseAdapter extends ButtonProcessorBase {

    public KeyboardActionsBaseAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executorService, SettingsDao settingsDao, VirtualInputStateRepository repository) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executorService, settingsDao, repository);
    }

    @Override
    protected void process() {
        gamepadObserver.getButtonEventStream()
                .filter(triggerFilter())
                .map(buttonPressMapper::map)
                .filter(purgingFilter())
                .map(q -> mapEvent(q, null, null))
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }
}
