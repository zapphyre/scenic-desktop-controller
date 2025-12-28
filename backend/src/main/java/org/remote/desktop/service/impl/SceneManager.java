package org.remote.desktop.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.provider.SceneNameContainer;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static org.zapphyre.function.FunHelper.chew;
import static org.zapphyre.function.FunHelper.funky;

@Service
@RequiredArgsConstructor
public class SceneManager implements SceneNameContainer, ApplicationListener<GpadCommandEvent> {
    private final List<Consumer<String>> recognizedSceneObservers = new LinkedList<>();
    private final List<Consumer<String>> forcedSceneObservers = new LinkedList<>();

    private final SceneDao sceneDao;

    @Getter
    private SceneDto forcedScene;
    @Getter
    private SceneDto lastRecognizedScene;

    private String lastRecognizedWindowName = "";

    @Override
    public void onApplicationEvent(GpadCommandEvent event) {
        if (event.getKeyPart().getKeyEvt().equals("SCENE_RESET"))
            forcedSceneObservers.forEach(p -> p.accept(lastRecognizedWindowName));

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
        return lastRecognizedWindowName;
    }

    public void nullifyForcedScene() {
        forcedScene = null;
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

    public void update() {
        recognizedSceneObservers.forEach(q -> q.accept(lastRecognizedWindowName));
    }

    @Override
    public void setRecognizedSceneName(String recognizedSceneName) {
        lastRecognizedWindowName = recognizedSceneName;
        recognizedSceneObservers.forEach(q -> q.accept(recognizedSceneName));
    }
}
