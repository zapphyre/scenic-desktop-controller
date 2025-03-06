package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;

import java.net.URI;

@Value
@Builder
public class WebSourceDef {
    URI baseURI;
    int port;
    String name;
}
