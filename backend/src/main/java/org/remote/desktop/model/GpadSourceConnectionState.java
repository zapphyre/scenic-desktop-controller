package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.asmus.model.SourceState;
import org.zapphyre.discovery.model.JmDnsProperties;

@Value
@Builder
@Jacksonized
public class GpadSourceConnectionState {
    SourceState  sourceState;
    JmDnsProperties  jmDnsProperties;
}
