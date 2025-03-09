package org.remote.desktop.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.model.JmDmsInstanceName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmDmsRegistry {

    private final JmDNS jmdns;
    private final JmDmsInstanceName name;

    @Value("${spring.application.name:gpadOs}")
    private String appName;

    @PostConstruct
    void init() throws IOException {
        log.info("registering JmDNS service");
        String instanceName = appName + "_" + name.uuid();
        log.info("registering JmDNS instance {}", instanceName);

        jmdns.registerService(ServiceInfo.create("_gevt._tcp.local.", instanceName, 8091, "hovno"));
    }
}
