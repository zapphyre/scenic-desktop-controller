package org.remote.desktop.provider.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.feign.CurrentSceneClient;
import org.remote.desktop.provider.XdoSceneProvider;

@RequiredArgsConstructor
public class NetworkXdoSceneProvider implements XdoSceneProvider {

    private final CurrentSceneClient currentSceneClient;

    @Override
    public String tryGetCurrentName() {
        return currentSceneClient.getCurrentSceneName();
    }
}
