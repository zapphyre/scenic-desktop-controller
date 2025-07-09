package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.config.FeignBuilder;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.model.SourceEvent;
import org.remote.desktop.processor.ArrowsAdapter;
import org.remote.desktop.processor.AxisAdapter;
import org.remote.desktop.processor.ButtonAdapter;
import org.remote.desktop.processor.DigitizedTriggerAdapter;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.source.ConnectableSource;
import org.remote.desktop.source.impl.EventSourceFactory;
import org.remote.desktop.source.impl.LocalSource;
import org.remote.desktop.source.impl.WebSource;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.model.WebSourceDef;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GpadHostRepository implements JmAutoRegistry {

    private final SettingsDao settingsDao;
    private final EventSourceFactory eventSourceFactory;

    private final Map<WebSourceDef, ConnectableSource> connectableSources = new HashMap<>();
    private final Sinks.Many<SourceEvent> sourceStateStream = Sinks.many().multicast().directBestEffort();

    @PostConstruct
    void init() {
        connectableSources.computeIfAbsent(EventSourceFactory.getLocalDef(), eventSourceFactory::produceLocalSource)
                .connect();
    }

    public ConnectableSource getLocalSource() {
        return connectableSources.get(EventSourceFactory.getLocalDef());
    }

    public void toggleSourceConnection(WebSourceDef def) {
        ConnectableSource connectableSource = connectableSources.get(def);

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
        connectableSources.computeIfAbsent(def, eventSourceFactory::produceSource);

        sourceStateStream.tryEmitNext(new SourceEvent(def, ESourceEvent.APPEARED));
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
        String instanceName = settingsDao.getInstanceName();

        return JmDnsProperties.builder()
                .greetingMessage("hi")
                .group("gevt")
                .instanceName(settingsDao.getInstanceName())
                .build();
    }

    public Flux<SourceEvent> getConnectedFlux() {
        return sourceStateStream.asFlux();
    }
}
