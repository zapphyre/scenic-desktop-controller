package org.remote.desktop.scene.impl;

import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.manager.SceneAware;
import org.remote.desktop.scene.BaseScene;

import java.util.concurrent.ScheduledFuture;

import static jxdotool.xDoToolUtil.appSwitchOn;

@Slf4j
public class TabSwitchScene extends BaseScene {
    public static int AXIS_MIN = -32_767;
    public static int AXIS_MAX = 32_767;
    public static int STEP = 6_000;

    private ScheduledFuture<?> future;
    private SceneAware manager;

    private int lastPosition;

    public TabSwitchScene() {
        appSwitchOn();
    }

    @Override
    public BaseScene leftTrigger(int pos) {
        if (pos == AXIS_MIN)
            return new DesktopScene();

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return true;
    }
}
