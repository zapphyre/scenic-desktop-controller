package org.remote.desktop.source.impl;

import lombok.RequiredArgsConstructor;
import org.asmus.service.JoyWorker;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.processor.ArrowsAdapter;
import org.remote.desktop.processor.AxisAdapter;
import org.remote.desktop.processor.ButtonAdapter;
import org.remote.desktop.processor.TriggerAdapter;
import org.remote.desktop.provider.impl.LocalXdoSceneProvider;
import org.remote.desktop.service.XdoSceneService;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.model.WebSourceDef;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@Component
@RequiredArgsConstructor
public class LocalSource extends BaseSource  {

    private final JoyWorker worker;
    private final ButtonAdapter buttonAdapter;
    private final TriggerAdapter triggerAdapter;
    private final ArrowsAdapter arrowsAdapter;
    private final AxisAdapter axisAdapter;

    private final SettingsDao settingsDao;
    private final XdoSceneService xdoSceneService;
    private final LocalXdoSceneProvider localXdoSceneProvider;

    @Override
    public ESourceEvent connect() {
        connectAndRemember(worker.getButtonStream()::subscribe, buttonAdapter::getButtonConsumer);
        connectAndRemember(worker.getAxisStream()::subscribe, arrowsAdapter::getArrowConsumer);

        connectAndRemember(worker.getAxisStream()::subscribe, triggerAdapter::getLeftTriggerProcessor);
        connectAndRemember(worker.getAxisStream()::subscribe, triggerAdapter::getRightTriggerProcessor);

        connectAndRemember(worker.getAxisStream()::subscribe, axisAdapter::getLeftStickProcessor);
        connectAndRemember(worker.getAxisStream()::subscribe, axisAdapter::getRightStickProcessor);

        connectAndRemember(leftStickStream().polarProducer(worker)::subscribe, axisAdapter::getLeftStickConsumer);
        connectAndRemember(rightStickStream().polarProducer(worker)::subscribe, axisAdapter::getRightStickConsumer);

        xdoSceneService.setSceneProvider(localXdoSceneProvider::tryGetCurrentName);

        return state = ESourceEvent.CONNECTED;
    }

    @Override
    public String describe() {
        return "local source";
    }

    @Override
    public boolean isConnected() {
        return state == ESourceEvent.CONNECTED;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    public WebSourceDef getDef() {
        return WebSourceDef.builder()
                .name(describe())
                .baseUrl("127.0.0.1")
                .port(settingsDao.getPort())
                .build();
    }
}
