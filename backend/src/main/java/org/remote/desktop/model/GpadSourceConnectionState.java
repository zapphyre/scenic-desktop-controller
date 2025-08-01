package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import org.asmus.model.SourceState;
import org.zapphyre.discovery.model.JmDnsProperties;

@Value
@Builder
public class GpadSourceConnectionState {
    SourceState  sourceState;
    JmDnsProperties  jmDnsProperties;
}
