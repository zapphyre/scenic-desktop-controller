package org.remote.desktop.source;

public interface ConnectableSource {

    void connect();

    void disconnect();

    String describe();

    boolean isConnected();

    default boolean isLocal() {
        return false;
    };

}
