package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.AppEventMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.service.GPadEventStreamService;
import org.remote.desktop.service.XdoSceneService;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TriggerActionMatcher {

    private final GPadEventStreamService gPadEventStreamService;
    private final ButtonPressMapper buttonPressMapper;
    private final XdoSceneService xdoSceneService;

    public Function<AppEventMapper,Function<ButtonActionDef, Flux<ApplicationEvent>>> actionPickPipeline = r -> p -> Flux.just(p)
            .flatMap(this::getNextSceneButtonEvent)
            .flatMap(q -> Flux.fromIterable(q.getActions())
                    .map(x -> r.mapEvent(p, q, x))
            );

    Mono<NextSceneXdoAction> getNextSceneButtonEvent(ButtonActionDef q) {
        return xdoSceneService.isSceneForced() ?
                getActionsOn(GPadEventStreamService::extractInheritedActions, xdoSceneService.getForcedScene(), q) :
                getActionsOn(GPadEventStreamService::relativeWindowNameActions, xdoSceneService.tryGetCurrentName(), q);
    }

    <P> Mono<NextSceneXdoAction> getActionsOn(BiFunction<GPadEventStreamService, P, Map<ActionMatch, NextSceneXdoAction>> paramGetter,
                                              P param,
                                              ButtonActionDef buttons) {
        return Mono.justOrEmpty(paramGetter.apply(gPadEventStreamService, param))
                .mapNotNull(getActionsForButtons(buttonPressMapper.map(buttons)))
                .map(q -> q.withButtonTrigger(buttons));
    }

    Function<Map<ActionMatch, NextSceneXdoAction>, NextSceneXdoAction> getActionsForButtons(ActionMatch def) {
        return q -> q.get(def);
    }
}
