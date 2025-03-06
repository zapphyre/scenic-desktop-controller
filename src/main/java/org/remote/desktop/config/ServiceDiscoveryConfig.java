package org.remote.desktop.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.source.EventSourceListener;
import org.springframework.context.annotation.Configuration;

import javax.jmdns.JmDNS;

@Configuration
@RequiredArgsConstructor
public class ServiceDiscoveryConfig {

    private final JmDNS jmdns;
    private final EventSourceListener eventSourceRepository;

    @PostConstruct
    void init() {
        jmdns.addServiceListener("_gevt.local.", eventSourceRepository);
    }
}
