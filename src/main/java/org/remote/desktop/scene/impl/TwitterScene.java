package org.remote.desktop.scene.impl;

import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TriggerPosition;
import org.remote.desktop.scene.BaseScene;

import static jxdotool.xDoToolUtil.*;
import static org.remote.desktop.scene.impl.TabSwitchScene.AXIS_MAX;
import static org.remote.desktop.scene.impl.TabSwitchScene.AXIS_MIN;

@Slf4j
public class TwitterScene extends BrowserScene {

    public static final String X_SCENE_NAME = "/ X";

    @Override
    public BaseScene btnY(GamepadEvent type) {
        if (type.getModifiers().contains(EButtonAxisMapping.BUMPER_LEFT))
            return super.btnY(type);

        if (type.isLongPress())
            pressKey("t");
        else
            pressKey("k");

        return this;
    }

    @Override
    public BaseScene btnA(GamepadEvent type) {
        if (tabSwitchOn)
            return super.btnA(type);

        if (type.isLongPress())
            pressKey("l");
        else
            pressKey("j");

        return this;
    }

    @Override
    public BaseScene btnB(GamepadEvent type) {
        if (type.isLongPress())
            pressKey("Ctrl+Enter");
        else
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
    public BaseScene start(GamepadEvent type) {
        pressKey("g+n");

        return this;
    }

    @Override
    public BaseScene select(GamepadEvent type) {
        pressKey("g+h");

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains(X_SCENE_NAME);
    }
}
