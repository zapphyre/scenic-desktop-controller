package org.remote.desktop.ui.model;

import reactor.core.publisher.Flux;

import java.util.function.Function;

public record StreamRepeaterDef<T>(Function<Flux<T>, Flux<T>> repeater) {
}
