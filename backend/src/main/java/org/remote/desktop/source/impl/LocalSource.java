package org.remote.desktop.source.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.asmus.service.JoyWorker;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.provider.SceneProvider;
import org.remote.desktop.provider.impl.LocalXdoSceneProvider;
import org.remote.desktop.service.impl.XdoSceneService;

@Slf4j
@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class LocalSource extends BaseSource {

    JoyWorker worker;

    SettingsDao settingsDao;
    XdoSceneService xdoSceneService;
    SceneProvider sceneProvider;

    @Override
    public ESourceEvent connect() {
        log.info("connecting local source");

        connectAndRemember(worker.getButtonStream(), buttonAdapter.getButtonConsumer());
        connectAndRemember(worker.getAxisStream(), arrowsAdapter.getArrowConsumer());

        connectAndRemember(worker.getAxisStream(), digitizedTriggerAdapter.getLeftTriggerProcessor());
        connectAndRemember(worker.getAxisStream(), digitizedTriggerAdapter.getRightTriggerProcessor());

        connectAndRemember(worker.getAxisStream(), digitizedTriggerAdapter.getLeftStepTriggerProcessor());
        connectAndRemember(worker.getAxisStream(), digitizedTriggerAdapter.getRightStepTriggerProcessor());

        connectAndRemember(worker.getAxisStream(), axisAdapter.leftAxis());
        connectAndRemember(worker.getAxisStream(), axisAdapter.rightAxis());

        xdoSceneService.setSceneProvider(sceneProvider::tryGetCurrentName);

        return state = ESourceEvent.CONNECTED;
    }

    @Override
    public boolean isConnected() {
        return state == ESourceEvent.CONNECTED;
    }
}
