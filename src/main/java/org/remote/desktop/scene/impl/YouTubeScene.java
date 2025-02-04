package org.remote.desktop.scene.impl;

import org.asmus.model.GamepadEvent;
import org.remote.desktop.scene.BaseScene;

import static jxdotool.xDoToolUtil.togglePlayYoutube;

public class YouTubeScene extends BrowserScene {

    @Override
    public BaseScene btnA(GamepadEvent type) {
        togglePlayYoutube();

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains("YouTube");
    }
}
