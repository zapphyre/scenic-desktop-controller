package org.remote.desktop.scene;

import jxdotool.xDoToolUtil;
import org.asmus.model.EType;
import org.asmus.model.QualifiedEType;
import org.remote.desktop.actuate.MouseCtrl;

import static jxdotool.xDoToolUtil.keyLeft;
import static jxdotool.xDoToolUtil.keyRight;

public abstract class BaseScene {

    public BaseScene up(QualifiedEType e) {

        if (e.getModifiers().contains(EType.BUMPER_LEFT))
            xDoToolUtil.pageUp();
        else
            xDoToolUtil.keyUp();

        return this;
    }

    public BaseScene down(QualifiedEType e) {
        if (e.getModifiers().contains(EType.BUMPER_LEFT))
            xDoToolUtil.pageDown();
        else
            xDoToolUtil.keyDown();

        return this;
    }

    public BaseScene left() {
        keyLeft();

        return this;
    }

    public BaseScene right() {
        keyRight();

        return this;
    }

    public abstract BaseScene appChoose(int pos);

    public BaseScene click() {
        MouseCtrl.click();

        return this;
    }
}
