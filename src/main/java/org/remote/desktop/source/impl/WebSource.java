package org.remote.desktop.source.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.asmus.model.PolarCoords;
import org.asmus.model.TimedValue;
import org.remote.desktop.component.AxisAdapter;
import org.remote.desktop.component.ButtonAdapter;
import org.remote.desktop.source.ConnectableSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Disposable;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSource implements ConnectableSource {

    private final List<Disposable> disposables = new LinkedList<>();

    private final ButtonAdapter buttonAdapter;
    private final AxisAdapter axisAdapter;

    private final WebClient.RequestHeadersUriSpec<?> spec;

    @Value("${remote.host.baseurl:http://localhost:9090}")
    private URI baseUrl;

    @SneakyThrows
    public EHStatus checkServerHealth() {
        UriComponents uri = UriComponentsBuilder.newInstance()
                .uri(baseUrl)
                .pathSegment("actuator", "health")
                .build();

        EHStatus status = EHStatus.UNKNOWN;
        try {
            status = new RestTemplate()
                    .getForObject(uri.toUriString(), Health.class)
                    .status();
        } catch (Exception e) {
        }

        return status;
    }

    @Override
    public boolean connect() {
        if (!isAvailable())
            return false;

        Disposable disposable = spec.uri("gpad/button")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<List<TimedValue>>() {
                })
                .subscribe(buttonAdapter.getButtonConsumer());

        Disposable disposable1 = spec.uri("gpad/axis")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Integer>>() {
                })
                .subscribe(buttonAdapter.getArrowConsumer());

        Disposable disposable2 = spec.uri("gpad/left-stick")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(PolarCoords.class)
                .subscribe(axisAdapter.getMouseConsumer());

        Disposable disposable3 = spec.uri("gpad/right-stick")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(PolarCoords.class)
                .subscribe(axisAdapter.getScrollConsumer());

        disposables.add(disposable);
        disposables.add(disposable1);
        disposables.add(disposable2);
        disposables.add(disposable3);

        return true;
    }

    @Override
    public boolean disconnect() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();

        return true;
    }

    @Override
    public boolean isAvailable() {
        return checkServerHealth() == EHStatus.UP;
    }

    @Override
    public String describe() {
        return "WebSource";
    }

    record Health(EHStatus status) {
    }

    enum EHStatus {
        UP, DOWN, UNKNOWN
    }
}
