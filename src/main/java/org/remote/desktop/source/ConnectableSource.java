package org.remote.desktop.source;

public interface ConnectableSource {

    boolean connect();

    boolean disconnect();

    boolean isAvailable();

    String describe();
}
