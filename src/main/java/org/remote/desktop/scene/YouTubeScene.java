package org.remote.desktop.scene;

import org.asmus.model.QualifiedEType;

import static jxdotool.xDoToolUtil.*;

public class YouTubeScene extends DesktopScene {

    @Override
    public BaseScene btnY(QualifiedEType type) {
        if (type.isLongPress())
            pressCtrlW();
        else
            pressCtrlT();

        return this;
    }

    @Override
    public BaseScene btnA(QualifiedEType type) {
        togglePlayYoutube();

        return this;
    }

    @Override
    public boolean windowTitleMaskMatches(String windowTitle) {
        return windowTitle.contains("YouTube");
    }
}
