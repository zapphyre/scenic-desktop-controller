package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.component.SourceManager;
import org.remote.desktop.model.SourceState;
import org.remote.desktop.model.WebSourceDef;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("source")
@RequiredArgsConstructor
public class SourceCtrl {

    private final SourceManager sourceManager;

    @GetMapping("connectedEventStream")
    public Flux<WebSourceDef> getConnectedEventStream() {
        return sourceManager.getConnectedFlux();
    }

    @GetMapping("disconnectedEventStream")
    public Flux<WebSourceDef> getDisconnectedEventStream() {
        return sourceManager.getDisconnectedFlux();
    }

    @GetMapping("all")
    public List<SourceState> getSourceStates() {
        return sourceManager.getOverallSourceStates();
    }

    @PutMapping("toggle")
    public void toggleSourceState(@RequestBody WebSourceDef def) {
        sourceManager.toggleSourceConnection(def);
    }
}
