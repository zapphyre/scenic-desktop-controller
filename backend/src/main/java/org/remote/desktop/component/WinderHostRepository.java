package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.config.FeignBuilder;
import org.remote.desktop.db.dao.SettingsDao;
import org.springframework.stereotype.Component;
import org.winder.api.WinderConstants;
import org.winder.api.WinderNativeConnectorApi;
import org.winder.common.model.EWinderOp;
import org.winder.common.model.WinderCommand;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.model.WebSourceDef;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WinderHostRepository implements JmAutoRegistry {

    private final SettingsDao settingsDao;
    private final FeignBuilder feignBuilder;
    private WinderNativeConnectorApi winderApi;

    public void sourceDiscovered(WebSourceDef webSourceDef) {
        System.out.println("WINDER sourceDiscovered: " + webSourceDef);
        winderApi = feignBuilder.buildWinderNativeConnectorApiClient(webSourceDef.getBaseUrl());
    }

    public void ff() {
        log.info("ff");

        if (Objects.nonNull(winderApi))
            winderApi.command(WinderCommand.builder()
                    .operation(EWinderOp.FF)
                    .build());
    }

    public void rw() {
        log.info("rw");

        if (Objects.nonNull(winderApi))
            winderApi.command(WinderCommand.builder()
                    .operation(EWinderOp.RW)
                    .build());
    }

    public void sourceLost(WebSourceDef s) {
        winderApi = null;
        log.warn("disconnected from winder: " + s.getName());
    }

    public JmDnsProperties getJmDnsProperties() {
        return JmDnsProperties.builder()
                .greetingMessage("hi")
                .group(WinderConstants.JM_GROUP)
                .instanceName("zbook_gpad")
                .build();
    }
}
