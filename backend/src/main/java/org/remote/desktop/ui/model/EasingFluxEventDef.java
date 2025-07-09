package org.remote.desktop.ui.model;

import org.asmus.model.PolarCoords;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;

public record EasingFluxEventDef<T>(Consumer<T> rightStickConsumer, Consumer<T> leftStickConsumer,
                                 Function<Flux<T>, Flux<T>> rightEasing,
                                 Function<Flux<T>, Flux<T>> leftEasing) {
}