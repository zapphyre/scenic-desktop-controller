package org.remote.desktop.source.impl;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.TimedValue;
import org.remote.desktop.component.GpadHostRepository;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.model.GpadSourceConnectionState;
import org.remote.desktop.service.impl.SourcesService;
import org.remote.desktop.source.ConnectableSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.remote.desktop.util.FluxUtil.pipe;

@Slf4j
@Value
@SuperBuilder
public class WebSource extends BaseSource {

    WebClient.RequestHeadersUriSpec<?> spec;
    ConnectableSource localSource;
    GpadHostRepository hostRepository;
    SettingsDao settingsDao;

    ParameterizedTypeReference<List<TimedValue>> BUTTON_RAW_DATA = new ParameterizedTypeReference<>() {
    };

    ParameterizedTypeReference<Map<String, Integer>> AXIS_RAW_DATA = new ParameterizedTypeReference<>() {
    };


    @Override
    public ESourceEvent connect() {
        log.info("connecting WEB source");

        connectAndRemember(spec.uri("raw-event/button")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(BUTTON_RAW_DATA)::subscribe, buttonAdapter::getButtonConsumer);

        connectAndRemember(spec.uri("raw-event/axis")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(AXIS_RAW_DATA)::subscribe, this::chainConsumers);

        connectAndRemember(spec.uri("source/source-state")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(GpadSourceConnectionState.class)::subscribe, hostRepository::handleDisconnect);

//        if (settingsDao.disconnectOnRemoteConnect())
//            localSource.disconnect();

//        xdoSceneService.setSceneProvider(sceneApi::getCurrentSceneName);

        return state = ESourceEvent.CONNECTED;
    }

    Consumer<Map<String, Integer>> chainConsumers() {
        return pipe(arrowsAdapter.getArrowConsumer(), digitizedTriggerAdapter.getLeftTriggerProcessor(),
                digitizedTriggerAdapter.getLeftTriggerProcessor(), digitizedTriggerAdapter.getRightTriggerProcessor(),
                digitizedTriggerAdapter.getLeftStepTriggerProcessor(), digitizedTriggerAdapter.getRightStepTriggerProcessor(),
                axisAdapter.leftAxis(), axisAdapter.rightAxis());
    }

    @Override
    public boolean isConnected() {
        return state == ESourceEvent.CONNECTED;
    }
}
