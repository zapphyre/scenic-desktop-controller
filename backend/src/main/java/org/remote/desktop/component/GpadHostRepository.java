package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.SourceState;
import org.asmus.service.JoyWorker;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.model.SourceEvent;
import org.remote.desktop.source.ConnectableSource;
import org.remote.desktop.source.impl.EventSourceFactory;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.intf.RegistryController;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.model.WebSourceDef;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class GpadHostRepository implements JmAutoRegistry {

    private final SettingsDao settingsDao;
    private final EventSourceFactory eventSourceFactory;
    private final JoyWorker joyWorker;

    private final JmDnsHostProperties hostProperties;
    private final RegistryController registryController;

    private final Map<WebSourceDef, ConnectableSource> connectableSources = new HashMap<>();
    private final Sinks.Many<SourceEvent> sourceStateStream = Sinks.many().multicast().directBestEffort();
    private final AtomicBoolean connected = new AtomicBoolean();

    @PostConstruct
    void init() {
        connectableSources.computeIfAbsent(EventSourceFactory.getLocalDef(), eventSourceFactory::produceLocalSource)
                .connect();

        joyWorker.getSourceStateStream()
                .distinctUntilChanged()
                .subscribe(this::announceSourceState);
    }

    @SneakyThrows
    void announceSourceState(SourceState state) {
        if (connected.get() == state.isConnected()) return;

        log.info("changing source state from {} to {}", connected.get(), state.isConnected());

        if (state.isConnected())
            registryController.register(getJmDnsProperties());
        else
            registryController.delist(getJmDnsProperties());

        connected.set(state.isConnected());
    }

    public ConnectableSource getLocalSource() {
        return connectableSources.get(EventSourceFactory.getLocalDef());
    }

    public void toggleSourceConnection(WebSourceDef def) {
        ConnectableSource connectableSource = connectableSources.get(def);

        ESourceEvent event = connectableSource.isConnected() ?
                connectableSource.disconnect() : connectableSource.connect();

        sourceStateStream.tryEmitNext(new SourceEvent(def, event));
    }

    public void sourceDiscovered(WebSourceDef def) {
        connectableSources.computeIfAbsent(def, q -> eventSourceFactory.produceSource(q, this));

        sourceStateStream.tryEmitNext(new SourceEvent(def, ESourceEvent.APPEARED));

        if (autoconnect(def))
            toggleSourceConnection(def);
    }

    public void sourceLost(WebSourceDef lost) {
        log.info("Source lost: " + lost.getName());
        WebSourceDef webSourceDef = connectableSources.keySet().stream()
                .filter(webSource -> webSource.getName().equals(lost.getName()))
                .findFirst()
                .orElseThrow();

        connectableSources.remove(webSourceDef);
        sourceStateStream.tryEmitNext(new SourceEvent(webSourceDef, ESourceEvent.LOST));
    }

    public List<SourceEvent> getOverallSourceStates() {
        return connectableSources.entrySet().stream()
                .map(q -> new SourceEvent(q.getKey(), q.getValue().isConnected() ?
                        ESourceEvent.CONNECTED : ESourceEvent.DISCONNECTED)
                )
                .toList();
    }

    boolean autoconnect(WebSourceDef def) {
        return Optional.ofNullable(settingsDao.getSettings().getAutoconnect()).orElseGet(Collections::emptySet)
                .contains(def.getName());
    }

    public JmDnsProperties getJmDnsProperties() {
        return JmDnsProperties.builder()
                .baseUrl(hostProperties.getMineIpAddress().getHostAddress())
                .greetingMessage("hi")
                .group("gevt")
                .instanceName(settingsDao.getInstanceName())
                .build();
    }

    public Flux<SourceEvent> getConnectedFlux() {
        return sourceStateStream.asFlux();
    }
}
