package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.pojo.KeyPart;
import org.remote.desktop.service.StateService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/state")
@RequiredArgsConstructor
public class StateCtrl {

    private final StateService stateService;

    @GetMapping("scene")
    public Flux<String> getSceneStateStream() {
        return stateService.getSceneStateFlux();
    }

    @GetMapping("keydown")
    public Flux<KeyPart> getKeyDownStream() {
        return stateService.getKeydownStateFlux();
    }

    @PutMapping("issue")
    public void issueInvertedKeyCommand(@RequestBody KeyPart keyPart) {
        stateService.issueInvertedKeyCommand(keyPart);
    }

    @PutMapping("nullify-forced")
    public void nullifyForcedScene() {
        stateService.nullifyForced();
    }
}
