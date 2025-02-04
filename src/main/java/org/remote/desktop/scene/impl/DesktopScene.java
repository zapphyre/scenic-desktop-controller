package org.remote.desktop.scene.impl;

import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.scene.BaseScene;

import static jxdotool.xDoToolUtil.appSwitchOff;
import static jxdotool.xDoToolUtil.pressKey;

public class DesktopScene extends BaseScene {

    public DesktopScene() {
        appSwitchOff();
    }

    @Override
    public BaseScene btnY(GamepadEvent type) {
        if (type.isLongPress())
            pressKey("Super_L+q");

        return this;
    }

    @Override
    public BaseScene leftBumper(GamepadEvent type) {
        if (type.getModifiers().contains(EButtonAxisMapping.BUMPER_RIGHT))
            pressKey("Super_L+Page_Up");
        else
            super.leftBumper(type);

        return this;
    }

    @Override
    public BaseScene rightBumper(GamepadEvent type) {
        if (type.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT))
            pressKey("Super_L+Page_Down");

        return this;
    }

    @Override
    public BaseScene leftTrigger(int pos) {
        return pos > 0 ? new TabSwitchScene() : this;
    }

}
