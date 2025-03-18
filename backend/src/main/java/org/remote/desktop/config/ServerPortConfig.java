package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

@Profile("!dev")
@Configuration
@RequiredArgsConstructor
public class ServerPortConfig implements WebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory> {

    private final SettingsDao settingsDao;

    @Value("${server.port:8081}")
    private int port;

    @Override
    public void customize(ConfigurableReactiveWebServerFactory factory) {
        Integer port = Optional.ofNullable(settingsDao)
                .map(SettingsDao::getPort)
                .orElse(this.port);

        factory.setPort(port);
    }
}
