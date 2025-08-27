package org.remote.desktop.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.event.GpadCommandEvent;
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
public class XdoSceneService implements ApplicationListener<GpadCommandEvent> {
    private final List<Consumer<String>> recognizedSceneObservers = new LinkedList<>();
    private final List<Consumer<String>> forcedSceneObservers = new LinkedList<>();

    private final SceneDao  sceneDao;

    @Setter
    private Supplier<String> sceneProvider;

    public XdoSceneService(LocalXdoSceneProvider localXdoSceneProvider, SceneDao sceneDao) {
        this.sceneProvider = localXdoSceneProvider::tryGetCurrentName;
        this.sceneDao = sceneDao;
    }

    @Getter
    private SceneDto forcedScene;
    private SceneDto lastRecognizedScene;

    private String lastRecognizedWindowName = "";

    public SceneDto saveLastRecognizedScene(SceneDto sceneDto) {
        return lastRecognizedScene = sceneDto;
    }

    @Override
    public void onApplicationEvent(GpadCommandEvent event) {
        Optional.of(event)
                .map(GpadCommandEvent::getNextScene)
                .map(q -> {
                    lastRecognizedWindowName = q.getWindowName();
                    return forcedScene = q;
                })
                .ifPresent(q -> forcedSceneObservers.forEach(p -> p.accept((q).getName())));
    }

    public void forceScene(SceneDto scene) {
        lastRecognizedWindowName = scene.getWindowName();

        forcedScene = lastRecognizedScene = scene;
    }

    public String tryGetCurrentName() {
        String windowName = sceneProvider.get();

        System.out.println("Current name: " + windowName);

        if (!windowName.equals(lastRecognizedWindowName))
            recognizedSceneObservers.forEach(p -> {
                p.accept(windowName);
            });

        return lastRecognizedWindowName = windowName;
    }

    public void nullifyForcedScene() {
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

    public SceneDto getLastScene() {
        return sceneDao.getSceneForWindowNameOrBase(lastRecognizedWindowName);
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
