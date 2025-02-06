package org.remote.desktop.scene.impl;

import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;

import java.util.function.Predicate;

import static jxdotool.xDoToolUtil.pressKey;
import static org.remote.desktop.DesktopRemoteMain.metaKeysUp;

@Slf4j
public class SystemWideScene {

    public static Predicate<GamepadEvent> leftBumper = q -> q.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT);
    public static Predicate<GamepadEvent> rightBumper = q -> q.getModifiers().contains(EButtonAxisMapping.BUMPER_RIGHT);
    public static Predicate<GamepadEvent> bothBumpers = leftBumper.and(rightBumper);

    public static void volumeUp(GamepadEvent type) {
        pressKey("XF86AudioRaiseVolume");
    }

    public static void volumeDown(GamepadEvent type) {
        pressKey("XF86AudioLowerVolume");
    }

    public static void resetMetaKeys(GamepadEvent evt) {
        log.info("resetMetaKeys");
            metaKeysUp();
    }
}
