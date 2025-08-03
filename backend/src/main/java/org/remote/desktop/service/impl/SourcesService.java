package org.remote.desktop.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.model.SourceState;
import org.asmus.service.JoyWorker;
import org.remote.desktop.component.GpadHostRepository;
import org.remote.desktop.model.GpadSourceConnectionState;
import org.springframework.stereotype.Service;
import org.zapphyre.discovery.intf.RegistryController;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.model.WebSourceDef;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;

import java.io.IOException;
import java.util.function.Consumer;

//@Service
@RequiredArgsConstructor
public class SourcesService {

    private final RegistryController registryController;
    private final GpadHostRepository  gpadHostRepository;
    private final JmDnsHostProperties hostProperties;
    private final JoyWorker joyWorker;

    @PostConstruct
    void init() {
        joyWorker.getSourceStateStream()
                .filter(SourceState::isConnected)
                .subscribe(q -> {
                    try {
                        registryController.register(gpadHostRepository.getJmDnsProperties());
                    } catch (IOException e) {
                    }
                });
    }

    public Consumer<GpadSourceConnectionState> handleDisconnect() {
        return sourceState -> {
            if (sourceState.getSourceState().isConnected()) return;

            registryController.delist(sourceState.getJmDnsProperties());
            gpadHostRepository.toggleSourceConnection(map(sourceState.getJmDnsProperties()));
        };
    }

    WebSourceDef map(JmDnsProperties p) {
        return WebSourceDef.builder()
                .baseUrl(hostProperties.getMineIpAddress().getHostAddress())
                .name(p.getInstanceName())
                .port(p.getPort())
                .build();
    }
}
