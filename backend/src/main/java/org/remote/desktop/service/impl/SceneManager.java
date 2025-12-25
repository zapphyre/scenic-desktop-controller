package org.remote.desktop.service.impl;

import lombok.Getter;
import lombok.Setter;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.provider.SceneProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.zapphyre.function.FunHelper.chew;
import static org.zapphyre.function.FunHelper.funky;

@Service
public class SceneManager implements ApplicationListener<GpadCommandEvent> {
    private final List<Consumer<String>> recognizedSceneObservers = new LinkedList<>();
    private final List<Consumer<String>> forcedSceneObservers = new LinkedList<>();

    private final SceneDao sceneDao;

    @Setter
    private Supplier<String> sceneProvider;

    public SceneManager(SceneProvider sceneProvider, SceneDao sceneDao) {
        this.sceneProvider = sceneProvider::tryGetCurrentName;
        this.sceneDao = sceneDao;
        this.lastRecognizedScene = sceneDao.getSceneForWindowNameOrBase(sceneProvider.tryGetCurrentName(), "DESKTOP");
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
                .map(funky(q -> forcedScene = q))
                .map(funky(chew(SceneDto::getWindowName, this::setLastDesktopRecognized)))
                .map(SceneDto::getName)
                .ifPresent(q -> forcedSceneObservers.forEach(p -> p.accept((q))));
    }

    public void setLastDesktopRecognized(String windowName) {
        Optional.ofNullable(sceneDao.getSceneByWindowName(windowName, "DESKTOP"))
                .filter(p -> p.getMode().getScenic())
                .ifPresent(p -> lastRecognizedScene = p);
    }

    public String tryGetCurrentName() {
        String windowName = sceneProvider.get();

        System.out.println("Current name: " + windowName);

        if (!windowName.equals(lastRecognizedWindowName))
            recognizedSceneObservers.forEach(p -> p.accept(windowName));

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
        return false;
    }

    public void update() {
        recognizedSceneObservers.forEach(q -> q.accept(lastRecognizedWindowName));
    }
}
