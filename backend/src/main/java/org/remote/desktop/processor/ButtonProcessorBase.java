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
import org.remote.desktop.model.EMode;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.event.WinderCommandEvent;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.service.impl.GPadEventStreamService;
import org.remote.desktop.service.impl.ModeService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.winder.common.model.EWinderOp;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
public abstract class ButtonProcessorBase implements AppEventMapper {

    protected final ButtonPressMapper buttonPressMapper;
    protected final ApplicationEventPublisher eventPublisher;
    protected final GPadEventStreamService gPadEventStreamService;
    protected final IntrospectedEventFactory gamepadObserver;
    protected final TriggerActionMatcher triggerActionMatcher;
    protected final ScheduledExecutorService executorService;
    private final ModeService modeService;
    protected final SettingsDao settingsDao;

    protected abstract Predicate<GamepadEvent> triggerFilter();

    protected EMode mode() {
        return EMode.DESKTOP;
    }

    protected Predicate<GamepadEvent> modeFilter() {
        return q -> modeService.getMode() == mode();
    }

    @PostConstruct
    protected void process() {
        easy(gamepadObserver.getButtonEventStream()
                .publishOn(Schedulers.fromExecutorService(executorService))
//                .filter(modeFilter())
                .filter(triggerFilter())
                .map(buttonPressMapper::map)
                .filter(purgingFilter())
                .doOnNext(this::qualificationExamine)
        )
                .map(triggerActionMatcher.appEventMapper(this))
                .flatMap(Flux::fromIterable)
                .subscribe(eventPublisher::publishEvent, Throwable::printStackTrace);
    }

    protected Flux<ButtonActionDef> easy(Flux<ButtonActionDef> gamepadEvents) {
        return gamepadEvents;
    }

    @Override
    public Function<XdoActionDto, ApplicationEvent> mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction) {
        return q ->
                switch (modeService.getMode()) {
                    case DESKTOP -> new XdoCommandEvent(this,
                            q.getKeyEvt(),
                            q.getKeyStrokes(),
                            sceneXdoAction.getNextScene(),
                            def.getTrigger(),
                            sceneXdoAction.getEventSourceScene().getWindowName(),
                            def.getModifiers(),
                            def.isLongPress()
                    );
                    case WINDER -> new WinderCommandEvent(this,
                            EWinderOp.valueOf(q.getEvent().getActions().getFirst().getKeyStrokes().getFirst())
                    );
                };
    }

    protected Predicate<ButtonActionDef> purgingFilter() {
        return q -> true;
    }

    protected void qualificationExamine(ButtonActionDef click) {
    }
}
