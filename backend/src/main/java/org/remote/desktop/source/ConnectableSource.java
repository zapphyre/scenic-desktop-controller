package org.remote.desktop.source;

import org.remote.desktop.model.ESourceEvent;

public interface ConnectableSource {

    ESourceEvent connect();

    ESourceEvent disconnect();

    String describe();

    boolean isConnected();

    default boolean isLocal() {
        return false;
    };

}
