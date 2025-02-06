package org.remote.desktop.scene.impl;

import jxdotool.xDoToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.scene.BaseScene;

@Slf4j
public class RPCS3Scene extends BaseScene {

    @Override
    public BaseScene select(GamepadEvent e) {
        if (e.getModifiers().contains(EButtonAxisMapping.OTHER))
            xDoToolUtil.pressKey("ctrl+s");

        return this;
    }

    @Override
    public BaseScene start(GamepadEvent e) {
        if (e.getModifiers().contains(EButtonAxisMapping.OTHER))
            xDoToolUtil.pressKey("ctrl+r");

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains("local_build");
    }
}
