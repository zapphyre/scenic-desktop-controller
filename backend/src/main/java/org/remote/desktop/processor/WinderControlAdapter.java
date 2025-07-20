package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.EMode;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.model.dto.XdoActionDto;
import org.remote.desktop.model.event.WinderCommandEvent;
import org.remote.desktop.service.impl.GPadEventStreamService;
import org.remote.desktop.service.impl.ModeService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.winder.common.model.EWinderOp;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.remote.desktop.util.ETriggerFilter.triggerUpTo;

//@Component
public class WinderControlAdapter extends ButtonAdapter {

    public WinderControlAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher,
                                GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver,
                                TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executorService,
                                ModeService modeService, SettingsDao settingsDao) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executorService, settingsDao, modeService);
    }

    @Override
    public Function<XdoActionDto, ApplicationEvent> mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction) {
        return q -> {
            return new WinderCommandEvent(this,
                    EWinderOp.valueOf(q.getEvent().getActions().getFirst().getKeyStrokes().getFirst())
            );
        };
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return triggerUpTo(EButtonAxisMapping.OTHER);
    }

    @Override
    protected EMode mode() {
        return EMode.WINDER;
    }
}
