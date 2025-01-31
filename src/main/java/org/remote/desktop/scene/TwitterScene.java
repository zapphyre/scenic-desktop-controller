package org.remote.desktop.scene;

import lombok.extern.slf4j.Slf4j;
import org.asmus.model.QualifiedEType;
import org.asmus.model.TriggerPosition;

import static jxdotool.xDoToolUtil.*;
import static org.remote.desktop.scene.TabSwitchScene.AXIS_MAX;
import static org.remote.desktop.scene.TabSwitchScene.AXIS_MIN;

@Slf4j
public class TwitterScene extends DesktopScene {

    @Override
    public BaseScene btnY(QualifiedEType type) {
        if (type.isLongPress())
            pressKey("t");
        else
            pressKey("k");

        return this;
    }

    @Override
    public BaseScene btnA(QualifiedEType type) {
        if (type.isLongPress())
            pressKey("l");
        else
            pressKey("j");

        return this;
    }

    @Override
    public BaseScene btnB() {
        enter();

        return this;
    }

    @Override
    public BaseScene btnX() {
        ffBack();

        return this;
    }

    int lastPos;
    boolean ready;

    @Override
    public BaseScene rightTrigger(TriggerPosition type) {
        if (type.getPosition() < lastPos && ready) {
            pressKey("j");
            ready = false;
        }

        if (type.getPosition() == AXIS_MAX)
            pressKey("period");

        lastPos = type.getPosition();

        if (!ready)
            ready = type.getPosition() == AXIS_MIN;

        return this;
    }

    @Override
    public BaseScene start() {
        pressKey("g+n");

        return this;
    }

    @Override
    public BaseScene select() {
        pressKey("g+h");

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains("/ X");
    }
}
