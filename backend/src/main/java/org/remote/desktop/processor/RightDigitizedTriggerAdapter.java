package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.InlineEasingFluxDecorator;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.ETriggerEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.GPadEventStreamService;
import org.remote.desktop.service.impl.ModeService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.util.FluxUtil;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.function.Predicate;

import static org.remote.desktop.util.ETriggerFilter.trigger;

@Component
public class RightDigitizedTriggerAdapter extends DigitizedTriggerAdapter {

    private final FluxUtil fluxUtil;

    public RightDigitizedTriggerAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, Scheduler executor, SettingsDao settingsDao, CacheManager cacheManager, SceneService sceneService, XdoSceneService xdoSceneService, ModeService modeService, FluxUtil fluxUtil) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executor, settingsDao, cacheManager, sceneService, xdoSceneService, modeService);
        this.fluxUtil = fluxUtil;
    }

    @Override
    protected InlineEasingFluxDecorator<ETriggerEvent, ButtonActionDef> getFluxRepeater(Flux<ButtonActionDef> gamepadEvents) {
        return new InlineEasingFluxDecorator<>(
                cacheManager,
                gamepadEvents,
                fluxUtil.GEeaserMap,
                SceneDto::getRightTriggerEaser,
                fluxUtil.triggerEventConsumerMap,
                SceneDto::getRightTriggerEvent
        );
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return trigger(EButtonAxisMapping.TRIGGER_RIGHT);
    }
}
