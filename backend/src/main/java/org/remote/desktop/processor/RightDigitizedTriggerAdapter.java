package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.component.GrokFluxRepeater;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.EAxisEvent;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.remote.desktop.util.ETriggerFilter.trigger;
import static org.remote.desktop.util.FluxUtil.GEeaserMap;

@Component
public class RightDigitizedTriggerAdapter extends DigitizedTriggerAdapter {

    public RightDigitizedTriggerAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executor, SettingsDao settingsDao, CacheManager cacheManager, SceneService sceneService, XdoSceneService xdoSceneService) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executor, settingsDao, cacheManager, sceneService, xdoSceneService);
    }

    @Override
    protected GrokFluxRepeater<ButtonActionDef> getGrokFluxRepeater(Flux<ButtonActionDef> gamepadEvents) {
        return  new GrokFluxRepeater<>(
                cacheManager,
                gamepadEvents,
                GEeaserMap,
                SceneDto::getRightTriggerEaser,
                Map.of(),
                q -> EAxisEvent.NOOP
        );
    }

    @Override
    protected Predicate<GamepadEvent> triggerFilter() {
        return trigger(EButtonAxisMapping.TRIGGER_RIGHT);
    }
}
