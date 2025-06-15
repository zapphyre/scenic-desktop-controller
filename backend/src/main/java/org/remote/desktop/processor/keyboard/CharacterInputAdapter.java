package org.remote.desktop.processor.keyboard;

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
import org.remote.desktop.model.event.keyboard.ButtonEvent;
import org.remote.desktop.processor.ButtonProcessorBase;
import org.remote.desktop.service.GPadEventStreamService;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.asmus.model.EQualificationType.PUSH;
import static org.asmus.model.EQualificationType.RELEASE;
import static org.remote.desktop.util.ETriggerFilter.triggerUpTo;

@Component
public class CharacterInputAdapter extends ButtonProcessorBase {

    private final List<EQualificationType> allowedQualifs = List.of(PUSH, RELEASE);

    public CharacterInputAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher,
                                 GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver,
                                 TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executorService,
                                 SettingsDao settingsDao) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executorService, settingsDao);
    }

    /*
     * at the end, this will produce only events for visual button change that mimics press
     * all actual widget input control is done through regular path as 'keyboard' scene (configurable in properties)
     * and set on UI so this way widget supports modifiers and long-press
     * */
    @Override
    protected void process() {
        gamepadObserver.getButtonEventStream()
                .filter(triggerFilter())
                .map(buttonPressMapper::map)
                .filter(purgingFilter())
                .map(q -> mapEvent(q, null).apply(null))
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerUpTo(EButtonAxisMapping.Y);
    }

    @Override
    protected Predicate<ButtonActionDef> purgingFilter() {
        return q -> allowedQualifs.contains(q.getQualified());
    }

    @Override
    public Function<XdoActionDto, ApplicationEvent> mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction) {
        return q ->
                new ButtonEvent(this, EActionButton.valueOf(def.getTrigger()), def.getQualified(), def.getModifiers(), def.isLongPress());
    }
}
