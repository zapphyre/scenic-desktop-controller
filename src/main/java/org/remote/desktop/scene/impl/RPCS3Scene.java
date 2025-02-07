package org.remote.desktop.scene.impl;

import jxdotool.xDoToolUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.scene.BaseScene;

@Slf4j
public class RPCS3Scene extends BaseScene {

    @SneakyThrows
    @Override
    public BaseScene select(GamepadEvent e) {
        if (e.getModifiers().contains(EButtonAxisMapping.OTHER)) {
            xDoToolUtil.pressKey("ctrl+s");
            Thread.sleep(4000);
            xDoToolUtil.pressKey("alt+Enter");
        }

        return this;
    }

    @SneakyThrows
    @Override
    public BaseScene start(GamepadEvent e) {
        if (e.getModifiers().contains(EButtonAxisMapping.OTHER)) {
            xDoToolUtil.pressKey("ctrl+r");
            Thread.sleep(3000);
            xDoToolUtil.pressKey("alt+Enter");
        }

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains("local_build");
    }
}
