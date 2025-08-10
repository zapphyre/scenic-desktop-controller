package org.remote.desktop.model.vto;

import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
public class SettingVto {
    Long id;
    String instanceName;
    Boolean allowNetworkDiscovery;
    String baseSceneName;
    boolean disconnectLocalOnRemoteConnection;
    String ipAddress;
    Integer port;
    Set<String> autoconnect = new HashSet<>();
}
