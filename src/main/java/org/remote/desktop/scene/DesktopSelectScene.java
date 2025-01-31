package org.remote.desktop.scene;

import jxdotool.xDoToolUtil;
import org.asmus.model.QualifiedEType;

import static jxdotool.xDoToolUtil.keyMeta;

public class DesktopSelectScene extends BaseScene {

    private boolean onceValid;

    public DesktopSelectScene(boolean onceValid) {
        this.onceValid = onceValid;
    }

    @Override
    public BaseScene left() {
        xDoToolUtil.pageUp();

        return this;
    }

    @Override
    public BaseScene right() {
        xDoToolUtil.pageDown();

        return this;
    }

    @Override
    public BaseScene home() {
        keyMeta();

        return new DesktopScene();
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return onceValid;
    }
}
