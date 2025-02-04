package org.remote.desktop.scene.impl;

import static org.remote.desktop.scene.impl.TradingViewScene.TRADINGVIEW_SCENE_NAME;
import static org.remote.desktop.scene.impl.TwitterScene.X_SCENE_NAME;

public class FirefoxScene extends BrowserScene {

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains("Firefox") &&
                !windowTitle.contains(X_SCENE_NAME) &&
                !windowTitle.contains(TRADINGVIEW_SCENE_NAME);
    }
}
