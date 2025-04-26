package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.GamepadEvent;
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

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.asmus.model.EButtonAxisMapping.LEFT_STICK_X;
import static org.asmus.model.EButtonAxisMapping.UP;
import static org.remote.desktop.util.ETriggerFilter.triggerBetween;

@Component
public class ArrowsAdapter extends ButtonProcessorBase {


    public ArrowsAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher,
                         GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver,
                         TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executorService,
                         SettingsDao settingsDao) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executorService, settingsDao);
    }

    public Consumer<Map<String, Integer>> getArrowConsumer() {
        return gamepadObserver.getArrowsStream()::processArrowEvents;
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerBetween(UP, LEFT_STICK_X);
    }

    @Override
    public ApplicationEvent mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction, XdoActionDto xdoAction) {
        return sceneXdoAction.getEventSourceScene().getName().equals(settingsDao.getSettings().getTextInputSceneName()) ?
                new PredictionControlEvent(this, null, def.getLogicalEventType(), def.getTrigger(), def.getModifiers(), def.isLongPress()) :
                super.mapEvent(def, sceneXdoAction, xdoAction);
    }
}
