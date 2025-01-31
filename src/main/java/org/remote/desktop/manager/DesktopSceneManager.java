package org.remote.desktop.manager;

import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EType;
import org.asmus.model.PolarCoords;
import org.asmus.model.QualifiedEType;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.scene.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Function;

import static jxdotool.xDoToolUtil.getCurrentWindowTitle;

@Slf4j
public class DesktopSceneManager implements SceneAware {

    private final List<BaseScene> namedScenes = List.of(new TwitterScene(), new TradingviewScene(), new YouTubeScene());
    private BaseScene currentScene = new DesktopScene();

    Function<SceneQEType<QualifiedEType>, BaseScene> applyButtonEvent = q ->
            switch (q.type().getType()) {
                case UP -> q.scene.up(q.type());
                case DOWN -> q.scene.down(q.type());

                case LEFT -> q.scene.left();
                case RIGHT -> q.scene.right();

                case BUMPER_LEFT -> q.scene.leftBumper(q.type);
                case BUMPER_RIGHT -> q.scene.rightBumper(q.type);

                case A -> q.scene.btnA(q.type);
                case Y -> q.scene.btnY(q.type);
                case X -> q.scene.btnX();
                case B -> q.scene.btnB();

                case START -> q.scene.start();
                case HOME -> q.scene.home();
                case SELECT -> q.scene.select();

                default -> q.scene;
            };

    <T> Function<T, SceneQEType<T>> windowedGeneric() {
        return q -> {
            String currentWindowTitle = getCurrentWindowTitle();

            return currentScene.windowTitleMaskMatches(currentWindowTitle) ?
                    new SceneQEType<>(q, currentScene) : namedScenes.stream()
                    .filter(s -> s.windowTitleMaskMatches(currentWindowTitle))
                    .findFirst().map(s -> new SceneQEType<>(q, s))
                    .orElse(new SceneQEType<>(q, new DesktopScene()));
        };
    }

    public Disposable handleButtons(Flux<QualifiedEType> fluxButtonEvents) {
        return fluxButtonEvents
                .log()
                .map(windowedGeneric())
                .map(applyButtonEvent)
                .subscribe(scene -> currentScene = scene);
    }

    public Disposable handleTriggerLeft(Flux<TriggerPosition> triggerPositionFlux) {
        return triggerPositionFlux
                .filter(q -> q.getType() == EType.TRIGGER_LEFT)
                .subscribe(q -> {
                            if ((currentScene = currentScene.leftTrigger(q.getPosition())) instanceof SelfTriggering s)
                                s.changeScene(this);
                        }
                );
    }

    public Disposable handleTriggerRight(Flux<TriggerPosition> triggerPositionFlux) {
        return triggerPositionFlux
                .filter(q -> q.getType() == EType.TRIGGER_RIGHT)
                .map(windowedGeneric())
                .subscribe(q -> currentScene = q.scene.rightTrigger(q.type()));
    }

    public Disposable handleLeftStick(Flux<PolarCoords> stickEvents) {
        return stickEvents.subscribe(MouseCtrl::moveMouse);
    }

    @Override
    public void setSceneCallback(BaseScene scene) {
        currentScene = scene;
    }

    public Disposable hanleRightStick(Flux<PolarCoords> rightStickStream) {
        return rightStickStream
                .subscribe(MouseCtrl::scroll);
    }

    record SceneQEType<T>(T type, BaseScene scene) {
    }
}
