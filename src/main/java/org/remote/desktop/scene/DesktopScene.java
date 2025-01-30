package org.remote.desktop.scene;

import static jxdotool.xDoToolUtil.appSwitchOff;

public class DesktopScene extends BaseScene {

    int previousPos;
    boolean readyToAppSwitch;

    public DesktopScene() {
        appSwitchOff();
        readyToAppSwitch = true;
    }

    public DesktopScene(boolean readyToAppSwitch) {
        appSwitchOff();
        this.readyToAppSwitch = readyToAppSwitch;
    }

    @Override
    public BaseScene appChoose(int pos) {
//        if (pos == AXIS_MIN) readyToAppSwitch = true;

//        System.out.printf("appChoose(%d)\n", pos);
//        BaseScene nextScene = null;
//
//        if (previousPos == AXIS_MIN && pos > AXIS_MIN)
//            nextScene = new TabSwitchScene();
//        else
//            nextScene = new DesktopScene();
//
//        previousPos = pos;
//
//        return nextScene;

        return pos > 0 ? new TabSwitchScene() : this;
    }
}
