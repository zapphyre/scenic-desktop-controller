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

import static org.remote.desktop.util.FluxUtil.pipe;

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
        Consumer<Map<String, Integer>> arrowConsumer = arrowsAdapter.getArrowConsumer();
        Consumer<Map<String, Integer>> leftTriggerProcessor = digitizedTriggerAdapter.getLeftTriggerProcessor();
        Consumer<Map<String, Integer>> rightTriggerProcessor = digitizedTriggerAdapter.getRightTriggerProcessor();
        Consumer<Map<String, Integer>> leftStepTriggerProcessor = digitizedTriggerAdapter.getLeftStepTriggerProcessor();
        Consumer<Map<String, Integer>> rightStepTriggerProcessor = digitizedTriggerAdapter.getRightStepTriggerProcessor();
        Consumer<Map<String, Integer>> leftAxisConsumer = axisAdapter.leftAxis();
        Consumer<Map<String, Integer>> rightAxisConsumer = axisAdapter.rightAxis();

        return pipe(arrowConsumer, leftTriggerProcessor, rightTriggerProcessor, leftStepTriggerProcessor,
                rightStepTriggerProcessor, leftAxisConsumer, rightAxisConsumer);
    }

    @Override
    public boolean isConnected() {
        return state == ESourceEvent.CONNECTED;
    }
}
