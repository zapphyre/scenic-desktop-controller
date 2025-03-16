package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class WebSourceDef {
    String baseUrl;
    int port;
    String name;
}
