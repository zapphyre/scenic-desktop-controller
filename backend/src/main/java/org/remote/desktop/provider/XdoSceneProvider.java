package org.remote.desktop.provider;

import org.springframework.lang.NonNull;

public interface XdoSceneProvider {

    @NonNull
    String tryGetCurrentName();
}
