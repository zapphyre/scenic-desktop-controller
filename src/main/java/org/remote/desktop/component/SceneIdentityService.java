package org.remote.desktop.component;

import org.springframework.stereotype.Service;

import java.util.Optional;

import static jxdotool.xDoToolUtil.getCurrentWindowTitle;

@Service
public class SceneIdentityService {

    private String lastRecognized;

    public String tryGetCurrentName() {
        return Optional.ofNullable(getCurrentWindowTitle())
                .map(q -> lastRecognized = q)
                .orElse(lastRecognized);
    }

    public String getLastSceneNameRecorded() {
        return lastRecognized;
    }
}
