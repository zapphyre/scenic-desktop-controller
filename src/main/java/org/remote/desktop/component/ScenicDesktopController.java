package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.facade.TimedButtonGamepadFactory;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.model.XdoActionVto;
import org.remote.desktop.service.SceneService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

import static jxdotool.xDoToolUtil.*;

@Component
@RequiredArgsConstructor
public class ScenicDesktopController {

    private final static TimedButtonGamepadFactory timedButtonGamepadFactory = new TimedButtonGamepadFactory();
    private final static List<Runnable> factoryDisposable = timedButtonGamepadFactory.watchForDevices(0, 1);

    private final SceneService sceneService;
    private SceneVto nextScene;

    @PostConstruct
    public void employController() {
        Flux.merge(timedButtonGamepadFactory.getButtonStream(), timedButtonGamepadFactory.getArrowsStream())
                .log()
                .map(q -> ButtonActionDef.builder()
                        .trigger(q.getType())
                        .modifiers(q.getModifiers())
                        .build())
                .flatMap(q -> Mono.justOrEmpty(sceneService.relativeWindowNameActions(getCurrentWindowTitle()))
                        .mapNotNull(p -> p.get(q))
                )
                .map(q -> {
                    if (nextScene == null)
                        nextScene = q.nextScene();
                    return q.actions();
                })
                .subscribe(act);
    }

    Consumer<List<XdoActionVto>> act = q -> q.forEach(p -> {
        switch (p.getKeyEvt()) {
            case PRESS:
                keydown(p.getKeyPress());
                break;
            case STROKE:
                pressKey(p.getKeyPress());
                break;
            case RELEASE:
                keyup(p.getKeyPress());
                break;
            case TIMEOUT:
                try {
                    Thread.sleep(Integer.parseInt(p.getKeyPress()));
                } catch (InterruptedException e) {
                }
                break;
        }
    });
}
