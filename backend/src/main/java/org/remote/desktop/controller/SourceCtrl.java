package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.component.SourceManager;
import org.remote.desktop.model.SourceEvent;
import org.remote.desktop.model.WebSourceDef;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("api/source")
@RequiredArgsConstructor
public class SourceCtrl {

    private final SourceManager sourceManager;

    @GetMapping("events")
    public Flux<SourceEvent> getConnectedEventStream() {
        return sourceManager.getConnectedFlux();
    }

    @GetMapping("all")
    public List<SourceEvent> getSourceStates() {
        return sourceManager.getOverallSourceStates();
    }

    @PutMapping("toggle")
    public void toggleSourceState(@RequestBody WebSourceDef def) {
        sourceManager.toggleSourceConnection(def);
    }
}
