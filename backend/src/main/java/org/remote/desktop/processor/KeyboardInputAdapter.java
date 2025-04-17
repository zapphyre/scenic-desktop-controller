package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.EQualificationType;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.event.ButtonEvent;
import org.remote.desktop.model.event.LongHoldEvent;
import org.remote.desktop.service.GPadEventStreamService;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

import static org.remote.desktop.util.EtriggerFilter.triggerUpTo;

@Component
public class KeyboardInputAdapter extends ButtonProcessorBase {

    public KeyboardInputAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executorService, SettingsDao settingsDao) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executorService, settingsDao);
    }

    @Override
    void process() {
        gamepadObserver.getButtonEventStream()
                .filter(triggerFilter())
                .map(buttonPressMapper::map)
                .filter(purgingFilter())
                .map(q -> mapEvent(q, null, null))
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerUpTo(EButtonAxisMapping.Y);
    }

    @Override
    protected Predicate<ButtonActionDef> purgingFilter() {
        return q -> q.getQualified().equals(EQualificationType.PUSH) ||
                q.getQualified().equals(EQualificationType.RELEASE) ||
                q.getQualified().equals(EQualificationType.LONG);
    }

    @Override
    public ApplicationEvent mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction, XdoActionDto xdoAction) {
        new LongHoldEvent(this, null);
        return new ButtonEvent(this, EActionButton.valueOf(def.getTrigger()));
    }
}
