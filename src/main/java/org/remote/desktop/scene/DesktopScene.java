package org.remote.desktop.scene;

import org.asmus.model.EType;
import org.asmus.model.QualifiedEType;

import static jxdotool.xDoToolUtil.appSwitchOff;
import static jxdotool.xDoToolUtil.keyMeta;

public class DesktopScene extends BaseScene {

    public DesktopScene() {
        appSwitchOff();
    }

    @Override
    public BaseScene leftBumper(QualifiedEType type) {
        if (!type.getModifiers().contains(EType.BUMPER_RIGHT))
            return super.leftBumper(type);

        keyMeta();

        return new DesktopSelectScene(true);
    }

    @Override
    public BaseScene leftTrigger(int pos) {
        return pos > 0 ? new TabSwitchScene() : this;
    }
}
