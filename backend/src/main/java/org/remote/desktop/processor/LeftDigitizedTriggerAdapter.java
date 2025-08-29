package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.InlineEasingFluxDecorator;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.ETriggerEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.service.impl.GPadEventStreamService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

import static org.remote.desktop.util.ETriggerFilter.trigger;
import static org.remote.desktop.util.FluxUtil.GEeaserMap;
import static org.remote.desktop.util.FluxUtil.triggerEventConsumerMap;

@Primary
@Component
public class LeftDigitizedTriggerAdapter extends DigitizedTriggerAdapter {

    public LeftDigitizedTriggerAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executor, SettingsDao settingsDao, CacheManager cacheManager, SceneService sceneService, XdoSceneService xdoSceneService) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executor, settingsDao, cacheManager, sceneService, xdoSceneService);
    }

    @Override
    protected InlineEasingFluxDecorator<ETriggerEvent, ButtonActionDef> getFluxRepeater(Flux<ButtonActionDef> gamepadEvents) {
        return new InlineEasingFluxDecorator<>(
                cacheManager,
                gamepadEvents,
                GEeaserMap,
                SceneDto::getLeftTriggerEaser,
                triggerEventConsumerMap,
                SceneDto::getLeftTriggerEvent
        );
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return trigger(EButtonAxisMapping.TRIGGER_LEFT);
    }
}
