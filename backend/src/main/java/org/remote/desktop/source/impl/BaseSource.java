package org.remote.desktop.source.impl;

import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.source.ConnectableSource;
import reactor.core.Disposable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseSource implements ConnectableSource {

    private final List<Disposable> disposables = new LinkedList<>();
    protected ESourceEvent state;

    protected  <T> void connectAndRemember(Function<Consumer<T>, Disposable> connector, Supplier<Consumer<T>> action) {
        disposables.add(connector.apply(action.get()));
    }

    @Override
    public ESourceEvent disconnect() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();

        return state = ESourceEvent.DISCONNECTED;
    }
}

