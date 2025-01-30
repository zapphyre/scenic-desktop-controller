package org.remote.desktop.scene;

import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.manager.SceneAware;

import java.util.concurrent.ScheduledFuture;

import static jxdotool.xDoToolUtil.*;

@Slf4j
public class TabSwitchScene extends BaseScene implements SelfTriggering {
    public static int AXIS_MIN = -32_767;
    public static int AXIS_MAX = 969;
    public static int STEP = 6_000;

    private ScheduledFuture<?> future;
    private SceneAware manager;

    private int lastPosition;

    public TabSwitchScene() {
        appSwitchOn();
    }

    @Override
    public BaseScene appChoose(int pos) {
        if (pos == AXIS_MIN)
            return new DesktopScene();

//        int currentPosition = pos / STEP;
//
//        if (currentPosition > lastPosition)
//            keyRight();
//        else if (currentPosition < lastPosition)
//            keyLeft();
//
//        lastPosition = currentPosition;
//
//        if (future != null)
//            future.cancel(true);
//
//        future = Executors.newSingleThreadScheduledExecutor().schedule(() -> {
//            log.info("setting app and changing scene to desktop");
//            this.manager.setSceneCallback(new DesktopScene(false));
//        }, 2000, TimeUnit.MILLISECONDS);

        return this;
    }

    @Override
    public void changeScene(SceneAware manager) {
        this.manager = manager;
    }
}
