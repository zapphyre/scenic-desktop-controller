package org.remote.desktop.model.vto;

import lombok.Value;

@Value
public class SettingVto {
    Long id;
    String instanceName;
    Boolean allowNetworkDiscovery;
    String baseSceneName;
}
