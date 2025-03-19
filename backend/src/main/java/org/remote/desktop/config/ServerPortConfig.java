package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!dev")
@Configuration
@RequiredArgsConstructor
public class ServerPortConfig implements WebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory> {

    private final SettingsDao settingsDao;

    @Override
    public void customize(ConfigurableReactiveWebServerFactory factory) {
        factory.setPort(settingsDao.getPort());
    }
}
