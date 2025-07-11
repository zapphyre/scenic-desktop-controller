package org.remote.desktop.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "settings")
public class SettingsProperties {
    private String instanceName;
    private String winderInstanceName;
    private Boolean allowNetworkDiscovery;
    private String baseSceneName;
    private Boolean disconnectLocalOnRemoteConnection;
    private String ipAddress;
    private Integer port;

    private String hintedIpAddress;
    private String textInputSceneName = "keyboard";
    private boolean persistentPreciseInput;

}
