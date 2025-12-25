package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.AxisReading;
import org.remote.desktop.component.InlineEasingFluxDecorator;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.ETriggerEvent;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.service.impl.GPadEventStreamService;
import org.remote.desktop.service.impl.ModeService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.SceneManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;

import java.util.function.Consumer;

import static org.zapphyre.function.FunHelper.chew;
import static org.zapphyre.function.FunHelper.glob;

public abstract class DigitizedTriggerAdapter extends ButtonProcessorBase {

    protected final CacheManager cacheManager;
    protected final SceneService sceneService;
    protected final SceneManager xdoSceneService;
    private final ModeService modeService;

    public DigitizedTriggerAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, Scheduler executor, SettingsDao settingsDao, CacheManager cacheManager, SceneService sceneService, SceneManager xdoSceneService, ModeService modeService) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executor, settingsDao);

        this.cacheManager = cacheManager;
        this.sceneService = sceneService;
        this.xdoSceneService = xdoSceneService;
        this.modeService = modeService;
    }

    protected abstract InlineEasingFluxDecorator<ETriggerEvent, ButtonActionDef> getFluxRepeater(Flux<ButtonActionDef> gamepadEvents);

    @Override
    protected Flux<ButtonActionDef> easy(Flux<ButtonActionDef> gamepadEvents) {
        Sinks.Many<ButtonActionDef> buttons = Sinks.many().multicast().directBestEffort();

        for (GamepadDto g : modeService.getAllGamepads()) {
            InlineEasingFluxDecorator<ETriggerEvent, ButtonActionDef> repeater = getFluxRepeater(
                    gamepadEvents.filter(q -> q.getDevice().name().equals(g.getName()))
            );

            glob(xdoSceneService::registerRecognizedSceneObserverChange, xdoSceneService::registerForcedSceneObserver)
                    .to(chew(sceneService.getSceneForModeAndWindowNameOrBase(g), repeater::setScene));

            repeater.getRepeatingStream().subscribe(buttons::tryEmitNext);
        }

        return buttons.asFlux();
    }

    public Consumer<AxisReading> getLeftTriggerProcessor() {
        return gamepadObserver.leftTriggerDigitizedProcessor()::processArrowEvents;
    }

    public Consumer<AxisReading> getRightTriggerProcessor() {
        return gamepadObserver.rightTriggerDigitizedProcessor()::processArrowEvents;
    }

    public Consumer<AxisReading> getLeftStepTriggerProcessor() {
        return gamepadObserver.leftDigitizedRangeTriggerStream()::processArrowEvents;
    }

    public Consumer<AxisReading> getRightStepTriggerProcessor() {
        return gamepadObserver.rightDigitizedRangeTriggerStream()::processArrowEvents;
    }
}
