package org.remote.desktop.scene.impl;

import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.remote.desktop.scene.BaseScene;

import static jxdotool.xDoToolUtil.*;

@Slf4j
public abstract class BrowserScene extends DesktopScene {

    protected boolean tabSwitchOn;

    public BaseScene rightBumper(GamepadEvent type) {
        if (type.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT))
            return super.rightBumper(type);

        if (tabSwitchOn = !tabSwitchOn)
            tabSwitchOn();
        else
            tabSwitchOff();

        return this;
    }

    @Override
    public BaseScene btnY(GamepadEvent type) {
        if (type.isLongPress())
            pressKey("Ctrl+w");
        else
            pressKey("Ctrl+t");

        return this;
    }

    @Override
    public BaseScene btnA(GamepadEvent type) {
        enter();
        tabSwitchOff();

        tabSwitchOn = false;

        return this;
    }

}
