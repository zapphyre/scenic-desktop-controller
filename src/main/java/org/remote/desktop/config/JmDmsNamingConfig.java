package org.remote.desktop.config;

import org.remote.desktop.model.JmDmsInstanceName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class JmDmsNamingConfig {

    @Bean
    public JmDmsInstanceName jmdmsInstanceName() {
        return new JmDmsInstanceName(UUID.randomUUID());
    }
}
