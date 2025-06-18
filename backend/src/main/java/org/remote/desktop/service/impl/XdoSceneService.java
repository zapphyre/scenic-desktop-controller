package org.remote.desktop.service.impl;

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
    private SceneDto lastRecognizedScene;

    private String lastRecognizedSceneName = "";

    public SceneDto saveLastRecognizedScene(SceneDto sceneDto) {
        return lastRecognizedScene = sceneDto;
    }

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        Optional.of(event)
                .map(XdoCommandEvent::getNextScene)
                .map(q -> {
                    lastRecognizedSceneName = q.getWindowName();
                    return forcedScene = q;
                })
                .ifPresent(q -> forcedSceneObservers.forEach(p -> p.accept((q).getName())));
    }

    public void forceScene(SceneDto scene) {
        lastRecognizedSceneName = scene.getWindowName();

        forcedScene = lastRecognizedScene = scene;
    }

    public String tryGetCurrentName() {
        String windowName = sceneProvider.get();

        System.out.println("Current name: " + windowName);
        if (lastRecognizedSceneName != null)
            System.out.println("last recognized scene: " + lastRecognizedSceneName);

        if (!windowName.equals(lastRecognizedSceneName))
            recognizedSceneObservers.forEach(p -> {
                System.out.println("notifying obs");
                p.accept(windowName);
            });

        return lastRecognizedSceneName = windowName;
    }

    public void nullifyForcedScene() {
        System.out.println("nullifyForcedScene");
        forcedScene = null;
        tryGetCurrentName();
    }

    public boolean isSceneForced() {
        return Objects.nonNull(forcedScene);
    }

    public void registerRecognizedSceneObserverChange(Consumer<String> observer) {
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
