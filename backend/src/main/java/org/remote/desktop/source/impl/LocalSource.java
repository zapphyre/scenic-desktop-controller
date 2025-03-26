package org.remote.desktop.source.impl;

import lombok.RequiredArgsConstructor;
import org.asmus.service.JoyWorker;
import org.remote.desktop.component.AxisAdapter;
import org.remote.desktop.component.ButtonAdapter;
import org.remote.desktop.component.TriggerAdapter;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.model.WebSourceDef;
import org.remote.desktop.source.ConnectableSource;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;

import java.util.LinkedList;
import java.util.List;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@Component
@RequiredArgsConstructor
public class LocalSource implements ConnectableSource {
    private final List<Disposable> disposables = new LinkedList<>();

    private final JoyWorker worker;
    private final ButtonAdapter buttonAdapter;
    private final TriggerAdapter triggerAdapter;
    private final AxisAdapter axisAdapter;
    private final SettingsDao settingsDao;

    private boolean connected = false;

    @Override
    public ESourceEvent connect() {
        Disposable disposable = worker.getButtonStream().subscribe(buttonAdapter.getButtonConsumer());
        Disposable disposable1 = worker.getAxisStream().subscribe(buttonAdapter.getArrowConsumer());
        worker.getAxisStream().subscribe(triggerAdapter.getTriggerProcessor());

        Disposable disposable2 = leftStickStream().polarProducer(worker).subscribe(axisAdapter::getLeftStickConsumer);
        Disposable disposable3 = rightStickStream().polarProducer(worker).subscribe(axisAdapter::getRightStickConsumer);

        disposables.add(disposable);
        disposables.add(disposable1);
        disposables.add(disposable2);
        disposables.add(disposable3);

        connected = true;
        return ESourceEvent.CONNECTED;
    }

    @Override
    public ESourceEvent disconnect() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();

        connected = false;
        return ESourceEvent.DISCONNECTED;
    }

    @Override
    public String describe() {
        return "local source";
    }

    @Override
    public boolean isConnected() {
        return connected;
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
