package org.remote.desktop.model;

import org.zapphyre.discovery.model.WebSourceDef;

public record SourceEvent(WebSourceDef def, ESourceEvent evt) {
}
