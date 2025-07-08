package org.remote.desktop.source.builder;

import org.remote.desktop.processor.AxisAdapter;
import org.remote.desktop.source.impl.BaseSource;

@FunctionalInterface
public interface AxisAdapterStep {

    BaseSource axisAdapter(AxisAdapter axisAdapter);
}
