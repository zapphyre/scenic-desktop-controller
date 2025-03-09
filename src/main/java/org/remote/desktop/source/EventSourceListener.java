package org.remote.desktop.source;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.mapper.EventSourceMapper;
import org.remote.desktop.model.JmDmsInstanceName;
import org.remote.desktop.model.WebSourceDef;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class EventSourceListener implements ServiceListener {

    private final JmDNS jmdns;
    private final EventSourceMapper mapper;
    private final JmDmsInstanceName name;
    private final Consumer<WebSourceDef> addObserver;
    private final Consumer<WebSourceDef> removeObserver;

    @Override
    public void serviceAdded(ServiceEvent event) {
        log.info("Service appeared: " + event.getName());

        jmdns.requestServiceInfo(event.getType(), event.getName());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        removeObserver.accept(mapper.map(event));
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        String randomPart = event.getName().split("_")[1];
        if (randomPart.equals(name.uuid().toString())) return;

        Optional.of(event)
                .map(mapper::map)
                .ifPresent(addObserver);
    }
}
