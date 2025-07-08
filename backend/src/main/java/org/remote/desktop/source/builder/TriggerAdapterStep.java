package org.remote.desktop.source.builder;

import org.remote.desktop.processor.DigitizedTriggerAdapter;

@FunctionalInterface
public interface TriggerAdapterStep {

    AxisAdapterStep triggerAdapter(DigitizedTriggerAdapter triggerAdapter);
}
