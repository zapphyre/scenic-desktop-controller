package org.remote.desktop;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.facade.TimedButtonGamepadFactory;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.manager.DesktopSceneManager;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;

import static jxdotool.xDoToolUtil.keyUp;

@Slf4j
public class DesktopRemoteMain {

    private final static TimedButtonGamepadFactory timedButtonGamepadFactory = new TimedButtonGamepadFactory();
    private final static DesktopSceneManager sceneManager = new DesktopSceneManager(DesktopRemoteMain::halt);
    private final static List<Runnable> factoryDisposable = timedButtonGamepadFactory.watchForDevices(0, 1);

    private static Disposable disposableButtons;
    private static Disposable disposableArrows;
    private static Disposable disposableLeftStick;
    private static Disposable disposableRightTrigger;
    private static Disposable disposableTrigger;
    private static Disposable disposableRightStick;
    private static Disposable systemEvents;

    @SneakyThrows
    public static void main(String[] args) {
        Flux<TriggerPosition> triggerPositionFlux = timedButtonGamepadFactory.getTriggerStream().publish().autoConnect();
        Flux<GamepadEvent> buttonStream = timedButtonGamepadFactory.getButtonStream();
        Flux<GamepadEvent> arrowsStream = timedButtonGamepadFactory.getArrowsStream();

        disposableButtons = sceneManager.handleButtons(buttonStream);
        disposableArrows = sceneManager.handleButtons(arrowsStream);
        systemEvents = sceneManager.handleSystemEvents(arrowsStream.mergeWith(buttonStream));
        disposableLeftStick = sceneManager.handleLeftStick(timedButtonGamepadFactory.getLeftStickStream());
        disposableRightStick = sceneManager.hanleRightStick(timedButtonGamepadFactory.getRightStickStream());
        disposableRightTrigger = sceneManager.handleTriggerRight(triggerPositionFlux);
        disposableTrigger = sceneManager.handleTriggerLeft(triggerPositionFlux);

        Runtime.getRuntime().addShutdownHook(new Thread(DesktopRemoteMain::teardown));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> halt(e));

        log.info("jpad-desktop-remote running!");
    }

    private static void halt(Throwable throwable) {
        log.warn("--halting--");
        log.warn("Caused by: {}", throwable.getMessage());
        teardown();
        factoryDisposable.forEach(Runnable::run);
        log.warn("--halting--");

        System.exit(1);
    }

    private static void teardown() {
        disposableButtons.dispose();
        disposableArrows.dispose();
        disposableLeftStick.dispose();
        disposableTrigger.dispose();
        disposableRightStick.dispose();
        disposableRightTrigger.dispose();
        systemEvents.dispose();

        metaKeysUp();
    }

    public static void metaKeysUp() {
        keyUp("Super_L");
        keyUp("Ctrl");
        keyUp("Alt");
    }
}
