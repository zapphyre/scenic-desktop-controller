package org.remote.desktop.source.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.service.JoyWorker;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.processor.ArrowsAdapter;
import org.remote.desktop.processor.AxisAdapter;
import org.remote.desktop.processor.ButtonAdapter;
import org.remote.desktop.processor.DigitizedTriggerAdapter;
import org.remote.desktop.provider.impl.LocalXdoSceneProvider;
import org.remote.desktop.service.impl.XdoSceneService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.zapphyre.discovery.model.WebSourceDef;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EventSourceFactory {

    private final ButtonAdapter buttonAdapter;
    private final ArrowsAdapter arrowsAdapter;
    private final DigitizedTriggerAdapter digitizedTriggerAdapter;
    private final AxisAdapter axisAdapter;
    private final JoyWorker worker;

    private final SettingsDao settingsDao;
    private final XdoSceneService xdoSceneService;
    private final LocalXdoSceneProvider localXdoSceneProvider;

    @Getter
    private LocalSource localSource;

    public LocalSource produceLocalSource(WebSourceDef def) {
        if (Objects.nonNull(localSource))
            return localSource;

        return localSource = LocalSource.builder()
                .worker(worker)
                .buttonAdapter(buttonAdapter)
                .arrowsAdapter(arrowsAdapter)
                .digitizedTriggerAdapter(digitizedTriggerAdapter)
                .axisAdapter(axisAdapter)
                .definition(def)
                .localXdoSceneProvider(localXdoSceneProvider)
                .xdoSceneService(xdoSceneService)
                .settingsDao(settingsDao)
                .build();
    }

    public static WebSourceDef getLocalDef() {
        return WebSourceDef.builder()
                .baseUrl("127.0.0.1")
                .name("Local Source")
                .port(8081)
                .build();
    }

    public WebSource produceSource(WebSourceDef def) {
        return WebSource.builder()
                .spec(getWebclient(def.getBaseUrl(), def.getPort()))
                .buttonAdapter(buttonAdapter)
                .arrowsAdapter(arrowsAdapter)
                .digitizedTriggerAdapter(digitizedTriggerAdapter)
                .axisAdapter(axisAdapter)
                .settingsDao(settingsDao)
                .definition(def)
                .localSource(localSource)
                .build();
    }

    WebClient.RequestHeadersUriSpec<?> getWebclient(String baseUrl, int port) {
        return WebClient.builder()
                .baseUrl(String.format(createUrl(baseUrl, port) + "/api/%s/", "raw-event"))
                .build()
                .get();
    }

    public static String createUrl(String baseUrl, int port) {
        return String.format("http://%s:%d", baseUrl, 8081);
    }
}
