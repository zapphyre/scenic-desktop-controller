package org.remote.desktop.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.source.EventSourceListener;
import org.springframework.context.annotation.Configuration;

import javax.jmdns.JmDNS;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ServiceDiscoveryConfig {

    private final JmDNS jmdns;
    private final EventSourceListener eventSourceRepository;

    @PostConstruct
    void init() {
        log.info("adding jmdns listener");
        jmdns.addServiceListener("_gevt._tcp.local.", eventSourceRepository);
    }
}
