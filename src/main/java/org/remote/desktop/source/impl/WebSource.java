package org.remote.desktop.source.impl;

import lombok.Builder;
import org.asmus.model.PolarCoords;
import org.asmus.model.TimedValue;
import org.remote.desktop.component.AxisAdapter;
import org.remote.desktop.component.ButtonAdapter;
import org.remote.desktop.source.ConnectableSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Builder
public class WebSource implements ConnectableSource {

    private final List<Disposable> disposables = new LinkedList<>();

    private final ButtonAdapter buttonAdapter;
    private final AxisAdapter axisAdapter;

    private final WebClient.RequestHeadersUriSpec<?> spec;
    private final String description;

    private boolean connected;

    @Override
    public void connect() {
        Disposable disposable = spec.uri("button")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<List<TimedValue>>() {
                })
                .subscribe(buttonAdapter.getButtonConsumer());

        Disposable disposable1 = spec.uri("axis")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Integer>>() {
                })
                .subscribe(buttonAdapter.getArrowConsumer());

        Disposable disposable2 = spec.uri("left-stick")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(PolarCoords.class)
                .subscribe(axisAdapter::getLeftStickConsumer);

        Disposable disposable3 = spec.uri("right-stick")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(PolarCoords.class)
                .subscribe(axisAdapter::getRightStickConsumer);

        disposables.add(disposable);
        disposables.add(disposable1);
        disposables.add(disposable2);
        disposables.add(disposable3);

        connected = true;
    }

    @Override
    public void disconnect() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();

        connected = false;
    }

    @Override
    public String describe() {
        return description;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}
