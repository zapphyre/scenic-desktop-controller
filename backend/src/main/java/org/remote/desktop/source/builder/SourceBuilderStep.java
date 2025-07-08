package org.remote.desktop.source.builder;

import org.remote.desktop.processor.ButtonAdapter;

@FunctionalInterface
public interface SourceBuilderStep {

    ArrowAdapterStep buttonAdapter(ButtonAdapter buttonAdapter);
}
