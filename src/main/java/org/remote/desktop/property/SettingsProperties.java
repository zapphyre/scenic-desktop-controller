package org.remote.desktop.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "settings")
public class SettingsProperties {
    private String instanceName;
    private Boolean allowNetworkDiscovery;
    private String baseSceneName;
    private Boolean disconnectLocalOnRemoteConnection;
}
