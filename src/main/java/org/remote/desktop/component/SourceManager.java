package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.SourceState;
import org.remote.desktop.model.WebSourceDef;
import org.remote.desktop.source.ConnectableSource;
import org.remote.desktop.source.impl.LocalSource;
import org.remote.desktop.source.impl.WebSource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SourceManager {

    private final Sinks.Many<WebSourceDef> connectedStream = Sinks.many().multicast().directBestEffort();
    private final Sinks.Many<WebSourceDef> disconnectedStream = Sinks.many().multicast().directBestEffort();

    private final Map<WebSourceDef, ConnectableSource> connectableSources = new HashMap<>();

    private final LocalSource localSource;
    private final ButtonAdapter buttonAdapter;
    private final AxisAdapter axisAdapter;

    @PostConstruct
    void init() {
        ConnectableSource local = connectableSources.computeIfAbsent(WebSourceDef.builder()
                .name("local")
                .build(), q -> localSource);

        local.connect();
    }

    public void toggleSourceConnection(WebSourceDef def) {
        ConnectableSource connectableSource = connectableSources.get(def);

        if (connectableSource.isConnected())
            connectableSource.disconnect();
        else
            connectableSource.connect();
    }

    public void sourceDiscovered(WebSourceDef def) {
        connectableSources.put(def, WebSource.builder()
                .spec(getWebclient(def.getBaseUrl(), def.getPort()))
                .axisAdapter(axisAdapter)
                .buttonAdapter(buttonAdapter)
                .description(def.getName())
                .build());
        connectedStream.tryEmitNext(def);
    }

    public void sourceLost(WebSourceDef def) {
        connectableSources.remove(def);
        disconnectedStream.tryEmitNext(def);
    }

    public Flux<WebSourceDef> getConnectedFlux() {
        return connectedStream.asFlux();
    }

    public Flux<WebSourceDef> getDisconnectedFlux() {
        return disconnectedStream.asFlux();
    }

    public List<SourceState> getOverallSourceStates() {
        return connectableSources.entrySet().stream()
                .map(q -> new SourceState(q.getKey(), q.getValue().isConnected()))
                .toList();
    }

    WebClient.RequestHeadersUriSpec<?> getWebclient(String baseUrl, int port) {
        return WebClient.builder()
                .baseUrl(String.format("http://%s:%d/%s/", baseUrl, port, "event"))
                .build()
                .get();
    }
}
