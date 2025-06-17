package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/hb")
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
