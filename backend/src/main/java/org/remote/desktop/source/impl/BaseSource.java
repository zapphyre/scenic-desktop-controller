package org.remote.desktop.source.impl;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.remote.desktop.model.ESourceEvent;
import org.remote.desktop.processor.ArrowsAdapter;
import org.remote.desktop.processor.ButtonAdapter;
import org.remote.desktop.processor.DigitizedTriggerAdapter;
import org.remote.desktop.processor.RepeatingAxisAdapter;
import org.remote.desktop.source.ConnectableSource;
import org.zapphyre.discovery.model.WebSourceDef;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public abstract class BaseSource implements ConnectableSource {

    private final List<Disposable> disposables = new ArrayList<>(10);
    protected ESourceEvent state;

    protected ButtonAdapter buttonAdapter;
    protected ArrowsAdapter arrowsAdapter;
    protected DigitizedTriggerAdapter digitizedTriggerAdapter;
    protected RepeatingAxisAdapter axisAdapter;

    @EqualsAndHashCode.Include
    protected WebSourceDef definition;

    protected <T> void connectAndRemember(Flux<T> connector, Consumer<T> action) {
        disposables.add(connector.subscribe(action));
    }

    public ESourceEvent disconnect() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();

        return state = ESourceEvent.DISCONNECTED;
    }

}

