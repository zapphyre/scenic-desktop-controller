package org.remote.desktop.source.impl;

import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.source.ConnectableSource;
import reactor.core.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseSource implements ConnectableSource {

    private final List<Disposable> disposables = new ArrayList<>(10);
    protected ESourceEvent state;

    protected <T> void connectAndRemember(Function<Consumer<T>, Disposable> connector, Supplier<Consumer<T>> action) {
        connector
                .andThen(disposables::add)
                .apply(action.get());
    }

    @Override
    public ESourceEvent disconnect() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();

        return state = ESourceEvent.DISCONNECTED;
    }
}

