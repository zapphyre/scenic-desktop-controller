package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.model.vto.SceneVto;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ActuatedStateRepository implements ApplicationListener<XdoCommandEvent> {
    private SceneVto forcedScene;

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        if (forcedScene == null)
            forcedScene = event.getNextScene();
    }

    public void nullifyForcedScene() {
        forcedScene = null;
    }

    public SceneVto getForcedScene() {
        return forcedScene;
    }

    public boolean isSceneForced() {
        return Objects.isNull(forcedScene);
    }

}
