package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ActionMatch;
import org.remote.desktop.model.AppEventMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.NextSceneXdoAction;
import org.remote.desktop.service.GPadEventStreamService;
import org.remote.desktop.service.XdoSceneService;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TriggerActionMatcher {

    private final GPadEventStreamService gPadEventStreamService;
    private final ButtonPressMapper buttonPressMapper;
    private final XdoSceneService xdoSceneService;

    public Function<ButtonActionDef, List<ApplicationEvent>> appEventMapper(AppEventMapper mapper) {
        return button -> {
            NextSceneXdoAction nextSceneXdoAction = getNextSceneButtonEventMapper(button);

            return Optional.ofNullable(nextSceneXdoAction)
                    .map(NextSceneXdoAction::getActions)
                    .orElseGet(Collections::emptyList).stream()
                    .map(mapper.mapEvent(button, nextSceneXdoAction))
                    .toList();
        };
    }

    // ActionMatch doesn't take in consideration qualifier; therefore filter might pass click for 'long' qualifier
    // that was isLong = false --which will match here the same way as unmodified release click would
    NextSceneXdoAction getNextSceneButtonEventMapper(ButtonActionDef button) {
        return Optional.of(button)
                .map(buttonPressMapper::map)
                .map(actionMapForCurrentScene(xdoSceneService.isSceneForced())::get)
                .orElse(null);
    }

    Map<ActionMatch, NextSceneXdoAction> actionMapForCurrentScene(boolean forced) {
        return forced ?
                gPadEventStreamService.extractInheritedActions(xdoSceneService.getForcedScene()) :
                gPadEventStreamService.relativeWindowNameActions(xdoSceneService.tryGetCurrentName());
    }

}
