package org.remote.desktop.scene;

import jxdotool.xDoToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.scene.impl.DesktopSelectScene;

import static jxdotool.xDoToolUtil.*;
import static org.remote.desktop.DesktopRemoteMain.metaKeysUp;

@Slf4j
public abstract class BaseScene {

    public BaseScene up(GamepadEvent e) {
        if (e.getModifiers().size() > 1)
            return this;

        if (e.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT))
            xDoToolUtil.pageUp();
        else
            xDoToolUtil.keyUp();

        return this;
    }

    public BaseScene down(GamepadEvent e) {
        if (e.getModifiers().size() > 1)
            return this;

        if (e.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT))
            xDoToolUtil.pageDown();
        else
            xDoToolUtil.keyDown();

        return this;
    }

    public BaseScene left() {
        keyLeft();

        return this;
    }

    public BaseScene right() {
        keyRight();

        return this;
    }

    public BaseScene leftTrigger(int pos) {
        return this;
    }

    public BaseScene leftBumper(GamepadEvent type) {
        MouseCtrl.click();

        return this;
    }

    public BaseScene rightBumper(GamepadEvent type) {
        return this;
    }

    public BaseScene btnA(GamepadEvent type) {
        return this;
    }

    public BaseScene btnB(GamepadEvent type) {
        return this;
    }

    public BaseScene btnX() {
        return this;
    }

    public BaseScene btnY(GamepadEvent type) {
        return this;
    }

    public boolean windowTitleMaskMatches(String windowTitle) {
        return false;
    }

    public BaseScene rightTrigger(TriggerPosition type) {
        return this;
    }

    public BaseScene start(GamepadEvent type) {
        return this;
    }

    public BaseScene home(GamepadEvent type) {
        if (type.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT) &&
                type.getModifiers().contains(EButtonAxisMapping.BUMPER_RIGHT)) {
            metaKeysUp();

            return this;
        }

        keyMeta();

        return new DesktopSelectScene(true);
    }

    public BaseScene select(GamepadEvent type) {
        return this;
    }
}
