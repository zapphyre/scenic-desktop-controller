package org.remote.desktop.source;

import org.remote.desktop.model.ESourceEvent;

public interface ConnectableSource {

    ESourceEvent connect();

    ESourceEvent disconnect();

    boolean isConnected();
}
