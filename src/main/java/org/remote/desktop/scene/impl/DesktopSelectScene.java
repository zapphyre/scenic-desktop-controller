package org.remote.desktop.scene.impl;

import jxdotool.xDoToolUtil;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.scene.BaseScene;

import static jxdotool.xDoToolUtil.keyMeta;

public class DesktopSelectScene extends BaseScene {

    private final boolean onceValid;

    public DesktopSelectScene(boolean onceValid) {
        this.onceValid = onceValid;
    }

    @Override
    public BaseScene left() {
        xDoToolUtil.pageUp();

        return this;
    }

    @Override
    public BaseScene right() {
        xDoToolUtil.pageDown();

        return this;
    }

    @Override
    public BaseScene home(GamepadEvent type) {
        keyMeta();

        return new DesktopScene();
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return onceValid;
    }
}
