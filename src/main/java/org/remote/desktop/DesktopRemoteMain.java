package org.remote.desktop;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.facade.TimedButtonGamepadFactory;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.manager.DesktopSceneManager;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Slf4j
public class DesktopRemoteMain {

    private final static DesktopSceneManager sceneManager = new DesktopSceneManager();

    @SneakyThrows
    public static void main(String[] args) {
        Flux<TriggerPosition> triggerPositionFlux = TimedButtonGamepadFactory.getTriggerStream().publish().autoConnect();

        Disposable disposableButtons = sceneManager.handleButtons(TimedButtonGamepadFactory.getButtonStream());
        Disposable disposableArrows = sceneManager.handleButtons(TimedButtonGamepadFactory.getArrowsStream());
        Disposable disposableLeftStick = sceneManager.handleLeftStick(TimedButtonGamepadFactory.getLeftStickStream());
        Disposable disposableRightTrigger = sceneManager.handleTriggerRight(triggerPositionFlux);
        Disposable disposableTrigger = sceneManager.handleTriggerLeft(triggerPositionFlux);
        Disposable disposableRightStick = sceneManager.hanleRightStick(TimedButtonGamepadFactory.getRightStickStream());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            disposableButtons.dispose();
            disposableArrows.dispose();
            disposableLeftStick.dispose();
            disposableTrigger.dispose();
            disposableRightStick.dispose();
            disposableRightTrigger.dispose();
        }));

        log.info("jpad-desktop-remote running!");
    }
}
