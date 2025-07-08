package org.remote.desktop.source.impl;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.asmus.model.TimedValue;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.source.ConnectableSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Value
@SuperBuilder
public class WebSource extends BaseSource {

     WebClient.RequestHeadersUriSpec<?> spec;

     ConnectableSource localSource;
     SettingsDao settingsDao;

     ParameterizedTypeReference<List<TimedValue>> BUTTON_RAW_DATA = new ParameterizedTypeReference<>() {
    };

     ParameterizedTypeReference<Map<String, Integer>> AXIS_RAW_DATA = new ParameterizedTypeReference<>() {
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
                .bodyToFlux(AXIS_RAW_DATA)::subscribe, this::chainConsumers);

        if (settingsDao.disconnectOnRemoteConnect())
            localSource.disconnect();

//        xdoSceneService.setSceneProvider(sceneApi::getCurrentSceneName);

        return state = ESourceEvent.CONNECTED;
    }

    Consumer<Map<String, Integer>> chainConsumers() {
        return q -> {
            arrowsAdapter.getArrowConsumer().accept(q);
            digitizedTriggerAdapter.getLeftTriggerProcessor().accept(q);
            digitizedTriggerAdapter.getRightTriggerProcessor().accept(q);
            digitizedTriggerAdapter.getLeftStepTriggerProcessor().accept(q);
            digitizedTriggerAdapter.getRightStepTriggerProcessor().accept(q);
            axisAdapter.leftAxis().accept(q);
            axisAdapter.rightAxis().accept(q);
        };
    }

    @Override
    public boolean isConnected() {
        return state == ESourceEvent.CONNECTED;
    }
}
