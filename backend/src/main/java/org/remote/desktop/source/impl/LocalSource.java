package org.remote.desktop.source.impl;

import lombok.RequiredArgsConstructor;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.processor.*;
import org.remote.desktop.provider.impl.LocalXdoSceneProvider;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.util.FluxUtil;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.model.WebSourceDef;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@Component
@RequiredArgsConstructor
public class LocalSource extends BaseSource {

    private final JoyWorker worker;
    private final ButtonAdapter buttonAdapter;
    private final DigitizedTriggerAdapter digitizedTriggerAdapter;
    private final ContinuousTriggerAdapter continuousTriggerAdapter;
    private final ArrowsAdapter arrowsAdapter;
    private final AxisAdapter axisAdapter;

    private final SettingsDao settingsDao;
    private final XdoSceneService xdoSceneService;
    private final LocalXdoSceneProvider localXdoSceneProvider;

    @Override
    public ESourceEvent connect() {
        connectAndRemember(worker.getButtonStream()::subscribe, buttonAdapter::getButtonConsumer);
        connectAndRemember(worker.getAxisStream()::subscribe, arrowsAdapter::getArrowConsumer);

        connectAndRemember(worker.getAxisStream()::subscribe, digitizedTriggerAdapter::getLeftTriggerProcessor);
        connectAndRemember(worker.getAxisStream()::subscribe, digitizedTriggerAdapter::getRightTriggerProcessor);

        connectAndRemember(worker.getAxisStream()::subscribe, axisAdapter::getLeftStickProcessor);
        connectAndRemember(worker.getAxisStream()::subscribe, axisAdapter::getRightStickProcessor);

//        connectAndRemember(worker.getAxisStream()::subscribe, continuousTriggerAdapter::getLeftContinuousTriggerProcessor);
//        connectAndRemember(worker.getAxisStream()::subscribe, continuousTriggerAdapter::getRightContinuousTriggerProcessor);


        /*
         *   consumer in .subscribe(..) -=can not=- be interchanged for method reference b/c it's being re-assigned
         *   in runtime on the instance;
         *   still, i want to use `connectAndRemember` for it manages disposable the standard way
         */
        connectAndRemember(_ ->
                FluxUtil.repeat(leftStickStream().polarProducer(worker), PolarCoords::isZero, 4)
                        .subscribe(q -> axisAdapter.getLeftStickConsumer().accept(q)), () -> null);

        // same...
        connectAndRemember(_ ->
                        rightStickStream().polarProducer(worker)
                                .subscribe(q -> axisAdapter.getRightStickConsumer().accept(q)),
                () -> null);

//        connectAndRemember(_ ->
//                        leftStickStream().polarProducer(worker)
//                                .subscribe(q -> axisAdapter.getLeftStickConsumer().accept(q)),
//                () -> null);

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
