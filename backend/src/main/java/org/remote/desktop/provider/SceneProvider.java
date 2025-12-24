package org.remote.desktop.provider;

import org.springframework.lang.NonNull;

public interface SceneProvider {

    @NonNull
    String tryGetCurrentName();
}
