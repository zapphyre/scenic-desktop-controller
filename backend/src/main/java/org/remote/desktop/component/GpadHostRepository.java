package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.SourceState;
import org.asmus.service.JoyWorker;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.model.GpadSourceConnectionState;
import org.remote.desktop.model.SourceEvent;
import org.remote.desktop.source.ConnectableSource;
import org.remote.desktop.source.impl.EventSourceFactory;
import org.remote.desktop.source.impl.WebSource;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.intf.RegistryController;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.model.WebSourceDef;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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
                .log()
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

        if (connectableSource == null) {
            System.out.println("null connectableSource: " + def);
            return;
        }

        ESourceEvent event = connectableSource.isConnected() ?
                connectableSource.disconnect() : connectableSource.connect();

        if (connectableSource instanceof WebSource ws) {
            ESourceEvent localState = ws.isConnected() ?
                    eventSourceFactory.getLocalSource().disconnect() :
                    eventSourceFactory.getLocalSource().connect();

            sourceStateStream.tryEmitNext(new SourceEvent(EventSourceFactory.getLocalDef(), localState));
        }

        sourceStateStream.tryEmitNext(new SourceEvent(def, event));
    }

    public void sourceDiscovered(WebSourceDef def) {
        if (connectableSources.containsKey(def))
            return;

        connectableSources.computeIfAbsent(def, q -> eventSourceFactory.produceSource(q, this));

        sourceStateStream.tryEmitNext(new SourceEvent(def, ESourceEvent.APPEARED));

//        if (settingsDao.getSettings().getAutoConnectHost().equals(InetAddress.ofLiteral(def.getBaseUrl())))
        if ("192.168.0.107".equals(def.getBaseUrl()))
            toggleSourceConnection(def);
    }

    public void sourceLost(WebSourceDef lost) {
        WebSourceDef webSourceDef = connectableSources.keySet().stream()
                .filter(webSource -> webSource.getName().equals(lost.getName()))
                .findFirst()
                .orElseThrow();

        connectableSources.remove(webSourceDef);
        log.info("Source lost: " + lost.getName());
        sourceStateStream.tryEmitNext(new SourceEvent(webSourceDef, ESourceEvent.LOST));
    }

    public List<SourceEvent> getOverallSourceStates() {
        return connectableSources.entrySet().stream()
                .map(q -> new SourceEvent(q.getKey(), q.getValue().isConnected() ?
                        ESourceEvent.CONNECTED : ESourceEvent.DISCONNECTED)
                )
                .toList();
    }

    public JmDnsProperties getJmDnsProperties() {
        return JmDnsProperties.builder()
                .baseUrl(hostProperties.getMineIpAddress().getHostAddress())
                .greetingMessage("hi")
                .group("gevt")
                .instanceName(settingsDao.getInstanceName())
                .build();
    }

    public Consumer<GpadSourceConnectionState> handleDisconnect() {
        return sourceState -> {
            if (sourceState.getSourceState().isConnected()) return;

            registryController.delist(sourceState.getJmDnsProperties());
            connected.set(false);

            toggleSourceConnection(map(sourceState.getJmDnsProperties()));
        };
    }

    WebSourceDef map(JmDnsProperties p) {
        return WebSourceDef.builder()
                .baseUrl(p.getBaseUrl())
                .name(p.getInstanceName())
                .port(p.getPort())
                .build();
    }

    public Flux<SourceEvent> getConnectedFlux() {
        return sourceStateStream.asFlux();
    }

    public Flux<GpadSourceConnectionState> getSourceStateFlux() {
        return joyWorker.getSourceStateStream()
                .map(q -> GpadSourceConnectionState.builder()
                        .jmDnsProperties(getJmDnsProperties())
                        .sourceState(q)
                        .build());
    }
}
