package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TimedValue;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.remote.desktop.util.ETriggerFilter.triggerUpTo;

@Component
public class ButtonAdapter extends ButtonProcessorBase {

    Predicate<ButtonActionDef> consumeLeftovers = gPadEventStreamService::consumeEventLeftovers;
    Predicate<ButtonActionDef> relevantQualification = gPadEventStreamService::isCurrentClickQualificationSceneRelevant;


    public ButtonAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher,
                         GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver,
                         TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executor, SettingsDao settingsDao) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executor, settingsDao);
    }

    @Override
    public ApplicationEvent mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction, XdoActionDto xdoAction) {
        return super.mapEvent(def, sceneXdoAction, xdoAction);
    }

    public Consumer<List<TimedValue>> getButtonConsumer() {
        return gamepadObserver.getButtonStream()::processButtonEvents;
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerUpTo(EButtonAxisMapping.OTHER);
    }

    @Override
    protected Predicate<ButtonActionDef> purgingFilter() {
        return consumeLeftovers.and(relevantQualification);
    }

    @Override
    protected void qualificationExamine(ButtonActionDef click) {
        gPadEventStreamService.computeRemainderFilter(click);
    }
}
