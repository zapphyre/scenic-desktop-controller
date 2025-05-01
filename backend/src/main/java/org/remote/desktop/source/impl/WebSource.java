package org.remote.desktop.source.impl;

import lombok.Builder;
import org.asmus.model.PolarCoords;
import org.asmus.model.TimedValue;
import org.remote.desktop.controller.SceneApi;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.processor.ArrowsAdapter;
import org.remote.desktop.processor.AxisAdapter;
import org.remote.desktop.processor.ButtonAdapter;
import org.remote.desktop.processor.TriggerAdapter;
import org.remote.desktop.service.XdoSceneService;
import org.remote.desktop.source.ConnectableSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Builder
public class WebSource extends BaseSource {

    private final ButtonAdapter buttonAdapter;
    private final ArrowsAdapter arrowsAdapter;
    private final TriggerAdapter triggerAdapter;
    private final AxisAdapter axisAdapter;

    private final WebClient.RequestHeadersUriSpec<?> spec;
    private final String description;

    private final ConnectableSource localSource;
    private final SettingsDao settingsDao;

    private final XdoSceneService xdoSceneService;
    private final SceneApi sceneApi;

    private final ParameterizedTypeReference<List<TimedValue>> BUTTON_RAW_DATA = new ParameterizedTypeReference<>() {
    };

    private final ParameterizedTypeReference<Map<String, Integer>> AXIS_RAW_DATA = new ParameterizedTypeReference<>() {
    };


    @Override
    public ESourceEvent connect() {
        connectAndRemember(spec.uri("button")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(BUTTON_RAW_DATA)::subscribe, buttonAdapter::getButtonConsumer);

        connectAndRemember(spec.uri("axis")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(AXIS_RAW_DATA)::subscribe, arrowsAdapter::getArrowConsumer);

        connectAndRemember(spec.uri("axis")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(AXIS_RAW_DATA)::subscribe, triggerAdapter::getLeftTriggerProcessor);

        connectAndRemember(spec.uri("axis")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(AXIS_RAW_DATA)::subscribe, triggerAdapter::getRightTriggerProcessor);

        connectAndRemember(spec.uri("left-stick")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(PolarCoords.class)::subscribe, axisAdapter::getLeftStickConsumer);

        connectAndRemember(spec.uri("right-stick")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(PolarCoords.class)::subscribe, axisAdapter::getRightStickConsumer);

        if (settingsDao.disconnectOnRemoteConnect())
            localSource.disconnect();

//        xdoSceneService.setSceneProvider(sceneApi::getCurrentSceneName);

        return state = ESourceEvent.CONNECTED;
    }

    @Override
    public String describe() {
        return description;
    }

    @Override
    public boolean isConnected() {
        return state == ESourceEvent.CONNECTED;
    }
}
