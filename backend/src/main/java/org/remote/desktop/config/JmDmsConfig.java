package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.component.SourceManager;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.mapper.EventSourceMapper;
import org.remote.desktop.source.EventSourceListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JmDmsConfig {

    private final SourceManager sourceManager;
    private final SettingsDao settingsDao;

    @Bean
    public JmDNS jmdns() throws IOException {
        log.info("Local host ip address: {}", settingsDao.getIpAddress());
        return JmDNS.create(InetAddress.getByName(settingsDao.getIpAddress()));
    }

    @Bean
    public EventSourceListener eventSourceRepository(JmDNS jmdns, EventSourceMapper mapper) {
        return new EventSourceListener(jmdns, mapper, settingsDao.getInstanceName(), sourceManager::sourceDiscovered, sourceManager::sourceLost);
    }
}
