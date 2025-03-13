package org.remote.desktop.model.dto;

import lombok.Value;

@Value
public class SettingDto {
    Long id;
    String instanceName;
    boolean allowNetworkDiscovery;
    String baseSceneName;
    boolean disconnectLocalOnRemoteConnection;
}
