package org.remote.desktop.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.provider.impl.LocalXdoSceneProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class XdoSceneService implements ApplicationListener<XdoCommandEvent> {
    private final List<Consumer<String>> recognizedSceneObservers = new LinkedList<>();
    private final List<Consumer<String>> forcedSceneObservers = new LinkedList<>();

    @Setter
    private Supplier<String> sceneProvider;

    public XdoSceneService(LocalXdoSceneProvider localXdoSceneProvider) {
        sceneProvider = localXdoSceneProvider::tryGetCurrentName;
    }

    @Getter
    private SceneDto forcedScene;

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        Optional.of(event)
                .map(XdoCommandEvent::getNextScene)
                .map(q -> forcedScene = q)
                .ifPresent(q -> forcedSceneObservers.forEach(p -> p.accept((q).getName())));
    }

    public String tryGetCurrentName() {
        String scene = sceneProvider.get();

        recognizedSceneObservers.forEach(p -> p.accept(scene));

        return scene;
    }

    public void nullifyForcedScene() {
        forcedScene = null;
    }

    public boolean isSceneForced() {
        return Objects.nonNull(forcedScene);
    }

    public void registerRecognizedSceneObserver(Consumer<String> observer) {
        recognizedSceneObservers.add(observer);
    }

    public void registerForcedSceneObserver(Consumer<String> observer) {
        forcedSceneObservers.add(observer);
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
