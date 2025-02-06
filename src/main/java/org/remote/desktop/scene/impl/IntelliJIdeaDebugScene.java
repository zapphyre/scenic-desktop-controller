package org.remote.desktop.scene.impl;

import org.asmus.model.GamepadEvent;
import org.remote.desktop.scene.BaseScene;

import static jxdotool.xDoToolUtil.pressKey;

public class IntelliJIdeaDebugScene extends BrowserScene {


    @Override
    public BaseScene btnA(GamepadEvent type) {
        pressKey("F8");

        return this;
    }

    @Override
    public BaseScene btnB(GamepadEvent type) {
        pressKey("F9");

        return this;
    }

    @Override
    public BaseScene btnX() {
        pressKey("F7");

        return this;
    }

    @Override
    public BaseScene btnY(GamepadEvent type) {
        pressKey("Shift+F8");

        return this;
    }


    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains(".java") || windowTitle.contains(".class");
    }
}


