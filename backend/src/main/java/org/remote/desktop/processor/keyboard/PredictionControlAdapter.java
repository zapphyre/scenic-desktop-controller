package org.remote.desktop.processor.keyboard;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.service.JoyWorker;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.event.keyboard.PredictionControlEvent;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

import static org.asmus.model.EButtonAxisMapping.*;

@Component
public class PredictionControlAdapter extends KeyboardActionsBaseAdapter {

    private final JoyWorker worker;
    private final List<EButtonAxisMapping> allowedButtons = List.of(LEFT, RIGHT, TRIGGER_RIGHT, BUMPER_LEFT);

    public PredictionControlAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executorService, SettingsDao settingsDao, JoyWorker worker) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executorService, settingsDao);
        this.worker = worker;
    }

    @Override
    protected void process() {
        super.process();
        worker.getAxisStream().subscribe(gamepadObserver.leftTriggerStream()::processArrowEvents);
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return q -> allowedButtons.contains(q.getType());
    }

    @Override
    public ApplicationEvent mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction, XdoActionDto xdoAction) {
        return new PredictionControlEvent(this, null, def.getLogicalEventType(), def.getTrigger());
    }
}
