package org.remote.desktop.source.impl;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.processor.ArrowsAdapter;
import org.remote.desktop.processor.AxisAdapter;
import org.remote.desktop.processor.ButtonAdapter;
import org.remote.desktop.processor.DigitizedTriggerAdapter;
import org.remote.desktop.source.ConnectableSource;
import org.zapphyre.discovery.model.WebSourceDef;
import reactor.core.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuperBuilder
@AllArgsConstructor
public abstract class BaseSource implements ConnectableSource {

    private final List<Disposable> disposables = new ArrayList<>(10);
    protected ESourceEvent state;

    protected ButtonAdapter buttonAdapter;
    protected ArrowsAdapter arrowsAdapter;
    protected DigitizedTriggerAdapter digitizedTriggerAdapter;
    protected AxisAdapter axisAdapter;

    @EqualsAndHashCode.Include
    protected WebSourceDef definition;

    protected <T> void connectAndRemember(Function<Consumer<T>, Disposable> connector, Supplier<Consumer<T>> action) {
        connector
                .andThen(disposables::add)
                .apply(action.get());
    }

    public ESourceEvent disconnect() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();

        return state = ESourceEvent.DISCONNECTED;
    }

}

