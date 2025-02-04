package org.remote.desktop.scene.impl;

import org.asmus.model.GamepadEvent;
import org.remote.desktop.scene.BaseScene;

import static jxdotool.xDoToolUtil.pressKey;

public class TradingViewScene extends BrowserScene {

    public static final String TRADINGVIEW_SCENE_NAME = "coinath";

    @Override
    public BaseScene btnX() {
        pressKey("Escape");

        return this;
    }

    @Override
    public BaseScene btnB(GamepadEvent type) {
        pressKey("Ctrl+r");

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains(TRADINGVIEW_SCENE_NAME);
    }
}
