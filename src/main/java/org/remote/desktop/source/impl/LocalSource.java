package org.remote.desktop.source.impl;

import lombok.RequiredArgsConstructor;
import org.asmus.service.JoyWorker;
import org.remote.desktop.component.AxisAdapter;
import org.remote.desktop.component.ButtonAdapter;
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
    private final AxisAdapter axisAdapter;

    @Override
    public boolean connect() {
        if (!isAvailable())
            return false;

        Disposable disposable = worker.getButtonStream().subscribe(buttonAdapter.getButtonConsumer());
        Disposable disposable1 = worker.getAxisStream().subscribe(buttonAdapter.getArrowConsumer());

        Disposable disposable2 = leftStickStream().polarProducer(worker).subscribe(axisAdapter::getLeftStickConsumer);
        Disposable disposable3 = rightStickStream().polarProducer(worker).subscribe(axisAdapter::getRightStickConsumer);

        disposables.add(disposable);
        disposables.add(disposable1);
        disposables.add(disposable2);
        disposables.add(disposable3);

        return true;
    }

    @Override
    public boolean disconnect() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();

        return true;
    }

    @Override
    public boolean isAvailable() {
        return worker.isConnected();
    }

    @Override
    public String describe() {
        return "local source";
    }
}
