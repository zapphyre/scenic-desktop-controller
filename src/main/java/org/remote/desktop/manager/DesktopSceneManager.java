package org.remote.desktop.manager;

import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EType;
import org.asmus.model.PolarCoords;
import org.asmus.model.QualifiedEType;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.scene.BaseScene;
import org.remote.desktop.scene.DesktopScene;
import org.remote.desktop.scene.SelfTriggering;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Slf4j
public class DesktopSceneManager implements SceneAware{

    private BaseScene currentScene = new DesktopScene();

    public Disposable handleButtons(Flux<QualifiedEType> fluxButtonEvents) {
        return fluxButtonEvents
                .log()
                .subscribe(e -> currentScene = switch (e.getType()) {
                    case UP -> currentScene.up(e);
                    case DOWN -> currentScene.down(e);

                    case LEFT -> currentScene.left();
                    case RIGHT -> currentScene.right();

                    case BUMPER_LEFT -> currentScene.click();

                    default -> currentScene;
                });
    }

    public Disposable handleTriggerLeft(Flux<TriggerPosition> triggerPositionFlux) {
        return triggerPositionFlux
                .filter(q -> q.getType() == EType.TRIGGER_LEFT)
                .subscribe(q -> {
                        if((currentScene = currentScene.appChoose(q.getPosition())) instanceof SelfTriggering s)
                            s.changeScene(this);
                        }
                );
    }

    public Disposable handleLeftStick(Flux<PolarCoords> stickEvents) {
        return stickEvents.subscribe(MouseCtrl::moveMouse);
    }

    @Override
    public void setSceneCallback(BaseScene scene) {
        currentScene = scene;
    }
}
