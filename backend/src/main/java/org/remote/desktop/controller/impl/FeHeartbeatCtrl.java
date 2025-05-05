package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.asmus.model.GamepadEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@RestController
@RequestMapping("api/hb")
@RequiredArgsConstructor
public class FeHeartbeatCtrl {

    @GetMapping
    public Flux<UUID> heartbeat() {
        return Flux.concat(
                Flux.just(UUID.randomUUID()),
                Flux.never() // Keeps the SSE connection open indefinitely
        );
    }
}
