package org.remote.desktop.scene;

import jxdotool.xDoToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EType;
import org.asmus.model.QualifiedEType;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.actuate.MouseCtrl;

import static jxdotool.xDoToolUtil.*;

@Slf4j
public abstract class BaseScene {

    public BaseScene up(QualifiedEType e) {

        if (e.getModifiers().contains(EType.BUMPER_LEFT))
            xDoToolUtil.pageUp();
        else
            xDoToolUtil.keyUp();

        return this;
    }

    public BaseScene down(QualifiedEType e) {
        if (e.getModifiers().contains(EType.BUMPER_LEFT))
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

    public BaseScene leftBumper(QualifiedEType type) {
        MouseCtrl.click();

        return this;
    }

    public BaseScene rightBumper(QualifiedEType type) {
        return this;
    }

    public BaseScene btnA(QualifiedEType type) {
        return this;
    }

    public BaseScene btnB() {
        return this;
    }

    public BaseScene btnX() {
        return this;
    }

    public BaseScene btnY(QualifiedEType type) {
        return this;
    }

    public boolean windowTitleMaskMatches(String windowTitle) {
        return false;
    }

    public BaseScene rightTrigger(TriggerPosition type) {
        return this;
    }

    public BaseScene start() {
        return this;
    }

    public BaseScene home() {
        return this;
    }

    public BaseScene select() {
        return this;
    }
}
