package org.remote.desktop.processor;

import org.asmus.builder.IntrospectedEventFactory;
import org.remote.desktop.component.InlineEasingFluxDecorator;
import org.remote.desktop.component.TriggerActionMatcher;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.ETriggerEvent;
import org.remote.desktop.service.impl.GPadEventStreamService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static org.zapphyre.function.FunHelper.chew;
import static org.zapphyre.function.FunHelper.glob;

public abstract class DigitizedTriggerAdapter extends ButtonProcessorBase {

    protected final CacheManager cacheManager;
    protected final SceneService sceneService;
    protected final XdoSceneService xdoSceneService;


    public DigitizedTriggerAdapter(ButtonPressMapper buttonPressMapper, ApplicationEventPublisher eventPublisher, GPadEventStreamService gPadEventStreamService, IntrospectedEventFactory gamepadObserver, TriggerActionMatcher triggerActionMatcher, ScheduledExecutorService executor, SettingsDao settingsDao, CacheManager cacheManager, SceneService sceneService, XdoSceneService xdoSceneService) {
        super(buttonPressMapper, eventPublisher, gPadEventStreamService, gamepadObserver, triggerActionMatcher, executor, settingsDao);

        this.cacheManager = cacheManager;
        this.sceneService = sceneService;
        this.xdoSceneService = xdoSceneService;
    }

    protected abstract InlineEasingFluxDecorator<ETriggerEvent, ButtonActionDef> getFluxRepeater(Flux<ButtonActionDef> gamepadEvents);

    @Override
    protected Flux<ButtonActionDef> easy(Flux<ButtonActionDef> gamepadEvents) {
        InlineEasingFluxDecorator<ETriggerEvent, ButtonActionDef> repeater = getFluxRepeater(gamepadEvents);

        glob(xdoSceneService::registerRecognizedSceneObserverChange, xdoSceneService::registerForcedSceneObserver)
                .to(chew(sceneService::getSceneForModeAndWindowNameOrBase, repeater::setScene));

        return repeater.getRepeatingStream()
                .publishOn(Schedulers.fromExecutorService(executorService))
                ;
    }

    public Consumer<Map<String, Integer>> getLeftTriggerProcessor() {
        return gamepadObserver.leftTriggerDigitizedProcessor()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getRightTriggerProcessor() {
        return gamepadObserver.rightTriggerDigitizedProcessor()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getLeftStepTriggerProcessor() {
        return gamepadObserver.leftDigitizedRangeTriggerStream()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getRightStepTriggerProcessor() {
        return gamepadObserver.rightDigitizedRangeTriggerStream()::processArrowEvents;
    }
}
