package org.remote.desktop.model.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class SettingDto {
    Long id;
    String instanceName;
    boolean allowNetworkDiscovery;
    String baseSceneName;
    boolean disconnectLocalOnRemoteConnection;
    Integer port;
    String ipAddress;
    boolean ipSetManually;
    String hintedIpAddress;
    String textInputSceneName = "keyboard";
    boolean persistentPreciseInput;

    @Builder.Default
    Set<String> autoconnect = new HashSet<>();
}
