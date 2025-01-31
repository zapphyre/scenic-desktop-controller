package org.remote.desktop.scene;

import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.manager.SceneAware;

import java.util.concurrent.ScheduledFuture;

import static jxdotool.xDoToolUtil.*;

@Slf4j
public class TabSwitchScene extends BaseScene implements SelfTriggering {
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
    public void changeScene(SceneAware manager) {
        this.manager = manager;
    }
}
