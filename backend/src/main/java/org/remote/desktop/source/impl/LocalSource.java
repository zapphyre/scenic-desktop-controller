package org.remote.desktop.source.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.asmus.service.JoyWorker;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.provider.impl.LocalXdoSceneProvider;
import org.remote.desktop.service.impl.XdoSceneService;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class LocalSource extends BaseSource {

    JoyWorker worker;

    SettingsDao settingsDao;
    XdoSceneService xdoSceneService;
    LocalXdoSceneProvider localXdoSceneProvider;

    @Override
    public ESourceEvent connect() {
        connectAndRemember(worker.getButtonStream()::subscribe, buttonAdapter::getButtonConsumer);
        connectAndRemember(worker.getAxisStream()::subscribe, arrowsAdapter::getArrowConsumer);

        connectAndRemember(worker.getAxisStream()::subscribe, digitizedTriggerAdapter::getLeftTriggerProcessor);
        connectAndRemember(worker.getAxisStream()::subscribe, digitizedTriggerAdapter::getRightTriggerProcessor);

        connectAndRemember(worker.getAxisStream()::subscribe, digitizedTriggerAdapter::getLeftStepTriggerProcessor);
        connectAndRemember(worker.getAxisStream()::subscribe, digitizedTriggerAdapter::getRightStepTriggerProcessor);

        connectAndRemember(worker.getAxisStream()::subscribe, axisAdapter::leftAxis);
        connectAndRemember(worker.getAxisStream()::subscribe, axisAdapter::rightAxis);

        xdoSceneService.setSceneProvider(localXdoSceneProvider::tryGetCurrentName);

        return state = ESourceEvent.CONNECTED;
    }

    @Override
    public boolean isConnected() {
        return state == ESourceEvent.CONNECTED;
    }
}
