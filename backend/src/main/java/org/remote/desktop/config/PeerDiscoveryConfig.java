package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.springframework.context.annotation.Configuration;
import org.zapphyre.discovery.intf.JmDnsPropertiesProvider;
import org.zapphyre.discovery.model.JmDnsProperties;

@Configuration
@RequiredArgsConstructor
public class PeerDiscoveryConfig implements JmDnsPropertiesProvider {

    private final SettingsDao settingsDao;

    @Override
    public JmDnsProperties getJmDnsProperties() {
        return JmDnsProperties.builder()
                .greetingMessage("hi")
                .group("_gevt._tcp.local.")
                .instanceName("zbook")
                .ipAddress(settingsDao.getIpAddress())
                .build();
    }
}
