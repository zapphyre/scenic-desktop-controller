package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.model.SourceEvent;
import org.remote.desktop.model.WebSourceDef;
import org.remote.desktop.processor.AxisAdapter;
import org.remote.desktop.processor.ButtonAdapter;
import org.remote.desktop.source.ConnectableSource;
import org.remote.desktop.source.impl.LocalSource;
import org.remote.desktop.source.impl.WebSource;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceManager {

    private final Sinks.Many<SourceEvent> sourceStateStream = Sinks.many().multicast().directBestEffort();

    private final Map<WebSourceDef, ConnectableSource> connectableSources = new HashMap<>();

    private final LocalSource localSource;
    private final ButtonAdapter buttonAdapter;
    private final AxisAdapter axisAdapter;
    private final SettingsDao settingsDao;
    private final ReactiveWebServerApplicationContext serverContext;

    @PostConstruct
    void init() {
        ConnectableSource local = connectableSources.computeIfAbsent(localSource.getDef(), q -> localSource);

        local.connect();
    }

    public void toggleSourceConnection(WebSourceDef def) {
        ConnectableSource connectableSource = connectableSources.get(def);

        ESourceEvent event = connectableSource.isConnected() ?
                connectableSource.disconnect() : connectableSource.connect();

        if (!def.equals(localSource.getDef()))
            if (event == ESourceEvent.CONNECTED)
                localSource.disconnect();
            else
                localSource.connect();

        sourceStateStream.tryEmitNext(new SourceEvent(def, event));
    }

    public void sourceDiscovered(WebSourceDef def) {
        connectableSources.put(def, WebSource.builder()
                .spec(getWebclient(def.getBaseUrl(), def.getPort()))
                .axisAdapter(axisAdapter)
                .buttonAdapter(buttonAdapter)
                .localSource(localSource)
                .settingsDao(settingsDao)
                .description(def.getName())
                .build());

        sourceStateStream.tryEmitNext(new SourceEvent(def, ESourceEvent.APPEARED));
    }

    public void sourceLost(String name) {
        WebSourceDef webSourceDef = connectableSources.keySet().stream()
                .filter(webSource -> webSource.getName().equals(name))
                .findFirst()
                .orElseThrow();

        connectableSources.remove(webSourceDef);
        log.info("Source lost: " + name);
        sourceStateStream.tryEmitNext(new SourceEvent(webSourceDef, ESourceEvent.LOST));
    }

    public Flux<SourceEvent> getConnectedFlux() {
        return sourceStateStream.asFlux();
    }

    public List<SourceEvent> getOverallSourceStates() {
        return connectableSources.entrySet().stream()
                .map(q -> new SourceEvent(q.getKey(), q.getValue().isConnected() ?
                        ESourceEvent.CONNECTED : ESourceEvent.DISCONNECTED)
                )
                .toList();
    }

    WebClient.RequestHeadersUriSpec<?> getWebclient(String baseUrl, int port) {
        return WebClient.builder()
                .baseUrl(String.format("http://%s:%d/api/%s/", baseUrl, port, "event"))
                .build()
                .get();
    }
}
