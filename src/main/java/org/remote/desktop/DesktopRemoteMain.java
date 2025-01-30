package org.remote.desktop;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.facade.TimedButtonGamepadFactory;
import org.remote.desktop.manager.DesktopSceneManager;
import reactor.core.Disposable;

@Slf4j
public class DesktopRemoteMain {

    private final static DesktopSceneManager sceneManager = new DesktopSceneManager();

    @SneakyThrows
    public static void main(String[] args) {
        Disposable disposableButtons = sceneManager.handleButtons(TimedButtonGamepadFactory.getButtonStream());
        Disposable disposableArrows = sceneManager.handleButtons(TimedButtonGamepadFactory.getArrowsStream());
        Disposable disposableLeftStick = sceneManager.handleLeftStick(TimedButtonGamepadFactory.getLeftStickStream());
        Disposable disposableTrigger = sceneManager.handleTriggerLeft(TimedButtonGamepadFactory.getTriggerStream());
//        Disposable disposableRightStick = sceneManager.handleLeftStick(TimedButtonGamepadFactory.getRightStickStream());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            disposableButtons.dispose();
            disposableArrows.dispose();
            disposableLeftStick.dispose();
            disposableTrigger.dispose();
//            disposableRightStick.dispose();
        }));

        log.info("jpad-desktop-remote running!");
    }
}
