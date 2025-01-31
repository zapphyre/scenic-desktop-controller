package org.remote.desktop.scene;

import org.asmus.model.QualifiedEType;

import static jxdotool.xDoToolUtil.*;

public class TradingviewScene extends DesktopScene {

    boolean tabSwitchOn;

    @Override
    public BaseScene btnX() {
        pressKey("Escape");

        return this;
    }

    @Override
    public BaseScene btnB() {
        pressKey("Ctrl+R");

        return this;
    }

    @Override
    public BaseScene rightBumper() {
        if (tabSwitchOn = !tabSwitchOn)
            tabSwitchOn();
        else
            tabSwitchOff();

        return this;
    }

    @Override
    public BaseScene btnA(QualifiedEType type) {
        enter();
        tabSwitchOff();

        tabSwitchOn = false;

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains("coinath");
    }
}
