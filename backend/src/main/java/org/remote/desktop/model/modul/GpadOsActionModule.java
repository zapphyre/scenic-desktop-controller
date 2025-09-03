package org.remote.desktop.model.modul;

public interface GpadOsActionModule {

    String getName();

    String eventTypes();

    boolean handleEvent(String event);
}
