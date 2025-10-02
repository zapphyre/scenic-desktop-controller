package org.remote.desktop.service.impl;

import lombok.Getter;
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

    private final SceneDao sceneDao;

    @Setter
    private Supplier<String> sceneProvider;

    public XdoSceneService(LocalXdoSceneProvider localXdoSceneProvider, SceneDao sceneDao) {
        this.sceneProvider = localXdoSceneProvider::tryGetCurrentName;
        this.sceneDao = sceneDao;
    }

    @Getter
    private SceneDto forcedScene;
    @Getter
    private SceneDto lastRecognizedScene;

    private String lastRecognizedWindowName = "";

    @Override
    public void onApplicationEvent(GpadCommandEvent event) {
        Optional.of(event)
                .map(GpadCommandEvent::getNextScene)
                .map(q -> {
                    lastRecognizedWindowName = q.getWindowName();

                    setLastDesktopRecognized(lastRecognizedWindowName);

                    return forcedScene = q;
                })
                .ifPresent(q -> forcedSceneObservers.forEach(p -> p.accept((q).getName())));
    }

    void setLastDesktopRecognized(String windowName) {
        Optional.ofNullable(sceneDao.getSceneByWindowName(windowName, "DESKTOP"))
                .filter(p -> p.getMode().getScenic())
                .ifPresent(p -> {
                    System.out.printf("re-setting scene %s now and setting %s%n", lastRecognizedWindowName, p.getName());
                    lastRecognizedScene = p;
                });
    }

    public String tryGetCurrentName() {
        String windowName = sceneProvider.get();

        System.out.println("Current name: " + windowName);

        if (!windowName.equals(lastRecognizedWindowName))
            recognizedSceneObservers.forEach(p -> {
                p.accept(windowName);
            });

        setLastDesktopRecognized(windowName);

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

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
