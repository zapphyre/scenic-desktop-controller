package org.remote.desktop.mode.modul;

import java.util.List;

public interface GpadOsActionModule {

    String getName();

    List<String> getNouns();

    boolean handleEvent(String verb, List<String> noun);
}
