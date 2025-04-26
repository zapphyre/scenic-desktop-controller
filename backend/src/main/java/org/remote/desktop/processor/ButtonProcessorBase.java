package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.AppEventMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.service.GPadEventStreamService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

@RequiredArgsConstructor
public abstract class ButtonProcessorBase implements AppEventMapper {

    protected final ButtonPressMapper buttonPressMapper;
    protected final ApplicationEventPublisher eventPublisher;
    protected final GPadEventStreamService gPadEventStreamService;
    protected final IntrospectedEventFactory gamepadObserver;
    protected final TriggerActionMatcher triggerActionMatcher;
    protected final ScheduledExecutorService executorService;
    protected final SettingsDao settingsDao;

    protected abstract Predicate<GamepadEvent> triggerFilter();

    @PostConstruct
    protected void process() {
        gamepadObserver.getButtonEventStream()
                .filter(triggerFilter())
                .map(buttonPressMapper::map)
                .filter(purgingFilter())
                .doOnNext(this::qualificationExamine)
                .flatMap(triggerActionMatcher.actionPickPipeline.apply(this))
                .publishOn(Schedulers.fromExecutorService(executorService))
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }

    @Override
    public ApplicationEvent mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction, XdoActionDto xdoAction) {
        return new  XdoCommandEvent(this,
                xdoAction.getKeyEvt(),
                xdoAction.getKeyStrokes(),
                sceneXdoAction.getNextScene(),
                def.getTrigger(),
                sceneXdoAction.getEventSourceScene().getWindowName(),
                def.getModifiers(),
                def.isLongPress()
        );
    }

    protected Predicate<ButtonActionDef> purgingFilter() {
        return q -> true;
    }

    protected void qualificationExamine(ButtonActionDef click) {
    }
}
