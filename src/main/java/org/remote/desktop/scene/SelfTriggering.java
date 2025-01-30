package org.remote.desktop.scene;

import org.remote.desktop.manager.SceneAware;

public interface SelfTriggering {
    void changeScene(SceneAware manager);
}
