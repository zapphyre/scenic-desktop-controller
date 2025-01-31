package org.remote.desktop.scene;

import org.asmus.model.EType;
import org.asmus.model.QualifiedEType;

import static jxdotool.xDoToolUtil.*;

public class DesktopScene extends BaseScene {

    public DesktopScene() {
        appSwitchOff();
    }

    @Override
    public BaseScene btnY(QualifiedEType type) {
        if (type.isLongPress())
            pressKey("Super_L+q");

        return this;
    }

    @Override
    public BaseScene leftBumper(QualifiedEType type) {
        if (type.getModifiers().contains(EType.BUMPER_RIGHT))
            pressKey("Super_L+Page_Up");
        else
            super.leftBumper(type);

        return this;
    }

    @Override
    public BaseScene rightBumper(QualifiedEType type) {
        if (type.getModifiers().contains(EType.BUMPER_LEFT))
            pressKey("Super_L+Page_Down");

        return this;
    }

    @Override
    public BaseScene home() {
        keyMeta();

        return new DesktopSelectScene(true);
    }

    @Override
    public BaseScene leftTrigger(int pos) {
        return pos > 0 ? new TabSwitchScene() : this;
    }
}
