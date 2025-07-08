package org.remote.desktop.source.builder;

import org.remote.desktop.processor.ArrowsAdapter;

@FunctionalInterface
public interface ArrowAdapterStep {

    TriggerAdapterStep arrowAdapter(ArrowsAdapter arrowsAdapter);
}
