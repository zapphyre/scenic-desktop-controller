package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.component.SourceManager;
import org.remote.desktop.mapper.EventSourceMapper;
import org.remote.desktop.source.EventSourceListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;

@Configuration
@RequiredArgsConstructor
public class JmDmsConfig {

    private final SourceManager sourceManager;

    @Bean
    public JmDNS jmdns() throws IOException {
        return JmDNS.create(InetAddress.getLocalHost());
    }

    @Bean
    public EventSourceListener eventSourceRepository(JmDNS jmdns, EventSourceMapper mapper) {
        return new EventSourceListener(jmdns, mapper, sourceManager::sourceDiscovered, sourceManager::sourceLost);
    }
}
