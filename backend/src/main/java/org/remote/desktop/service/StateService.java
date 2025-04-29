package org.remote.desktop.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.event.KeyboardStateRepository;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
public class StateService {

    private final Sinks.Many<String> sceneStateStream = Sinks.many().multicast().directBestEffort();
    private final Sinks.Many<KeyPart> keydownStateStream = Sinks.many().multicast().directBestEffort();

    private final KeyboardStateRepository keyboardStateRepository;
    private final XdoSceneService xdoSceneService;

    @PostConstruct
    void init() {
        xdoSceneService.registerForcedSceneObserver(sceneStateStream::tryEmitNext);
        xdoSceneService.registerRecognizedSceneObserver(sceneStateStream::tryEmitNext);
        keyboardStateRepository.registerXdoCommandObserver(keydownStateStream::tryEmitNext);
    }

    public void issueInvertedKeyCommand(KeyPart keyPart) {
        keyboardStateRepository.issueKeyupCommand(keyPart.invert());
    }

    public Flux<String> getSceneStateFlux() {
        return sceneStateStream.asFlux();
    }

    public Flux<KeyPart> getKeydownStateFlux() {
        return keydownStateStream.asFlux();
    }

    public void nullifyForced() {
        xdoSceneService.nullifyForcedScene();
        keyboardStateRepository.releaseAllPressedKeys();
    }
}
