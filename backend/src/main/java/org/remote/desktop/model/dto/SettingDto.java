package org.remote.desktop.model.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SettingDto {
    Long id;
    String instanceName;
    String winderInstanceName;
    boolean allowNetworkDiscovery;
    String baseSceneName;
    boolean disconnectLocalOnRemoteConnection;
    Integer port;
    String ipAddress;
    boolean ipSetManually;
    String hintedIpAddress;
    String textInputSceneName = "keyboard";
    boolean persistentPreciseInput;
}
