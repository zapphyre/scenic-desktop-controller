package org.remote.desktop.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.PolarCoords;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.scene.BaseScene;
import org.remote.desktop.scene.SelfTriggering;
import org.remote.desktop.scene.impl.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static jxdotool.xDoToolUtil.getCurrentWindowTitle;
import static org.remote.desktop.scene.impl.SystemWideScene.bothBumpers;

@Slf4j
@RequiredArgsConstructor
public class DesktopSceneManager implements SceneAware {

    private final Consumer<Throwable> haltOnError;

    private final List<BaseScene> namedScenes = List.of(
            new TwitterScene(), new FirefoxScene(), new TradingViewScene(), new RPCS3Scene(), new YouTubeScene()
    );

    private BaseScene currentScene = new DesktopScene();

    <T> Predicate<T> notScene(Class<? extends BaseScene> sceneClass) {
        return q -> !sceneClass.isInstance(currentScene);
    }

    Function<SceneQEType<GamepadEvent>, BaseScene> applyButtonEvent = q ->
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
                case B -> q.scene.btnB(q.type);

                case START -> q.scene.start(q.type);
                case OTHER -> q.scene.home(q.type);
                case SELECT -> q.scene.select(q.type);

                default -> q.scene;
            };

    <T> Function<T, SceneQEType<T>> forCurrentScene() {
        return q -> {
            String currentWindowTitle = getCurrentWindowTitle();

            return currentScene.windowTitleMaskMatches(currentWindowTitle) ?
                    new SceneQEType<>(q, currentScene) : namedScenes.stream()
                    .filter(s -> s.windowTitleMaskMatches(currentWindowTitle))
                    .findFirst().map(s -> new SceneQEType<>(q, s))
                    .orElse(new SceneQEType<>(q, new DesktopScene()));
        };
    }

    Predicate<TriggerPosition> filterTrigger(EButtonAxisMapping eType) {
        return q -> q.getType() == eType;
    }

    public Disposable handleButtons(Flux<GamepadEvent> fluxButtonEvents) {
        return fluxButtonEvents
                .log()
                .map(forCurrentScene())
                .map(applyButtonEvent)
                .subscribe(scene -> currentScene = scene, haltOnError);
    }

    public Disposable handleTriggerLeft(Flux<TriggerPosition> triggerPositionFlux) {
        return triggerPositionFlux
                .filter(notScene(RPCS3Scene.class))
                .filter(filterTrigger(EButtonAxisMapping.TRIGGER_LEFT))
                .subscribe(q -> {
                            if ((currentScene = currentScene.leftTrigger(q.getPosition())) instanceof SelfTriggering s)
                                s.changeScene(this);
                        }, haltOnError
                );
    }

    public Disposable handleTriggerRight(Flux<TriggerPosition> triggerPositionFlux) {
        return triggerPositionFlux
                .filter(notScene(RPCS3Scene.class))
                .filter(filterTrigger(EButtonAxisMapping.TRIGGER_RIGHT))
                .map(forCurrentScene())
                .subscribe(q -> currentScene = q.scene.rightTrigger(q.type()), haltOnError);
    }

    public Disposable handleLeftStick(Flux<PolarCoords> stickEvents) {
        return stickEvents
                .filter(notScene(RPCS3Scene.class))
                .subscribe(MouseCtrl::moveMouse);
    }

    @Override
    public void setSceneCallback(BaseScene scene) {
        currentScene = scene;
    }

    public Disposable hanleRightStick(Flux<PolarCoords> rightStickStream) {
        return rightStickStream
                .filter(notScene(RPCS3Scene.class))
                .subscribe(MouseCtrl::scroll, haltOnError);
    }

    public Disposable handleSystemEvents(Flux<GamepadEvent> buttonStream) {
        return buttonStream
                .filter(bothBumpers)
                .subscribe(q -> {
                    switch (q.getType()) {
                        case UP: SystemWideScene.volumeUp(q); break;
                        case DOWN: SystemWideScene.volumeDown(q); break;
                    }
                }, haltOnError);
    }

    record SceneQEType<T>(T type, BaseScene scene) {
    }
}
