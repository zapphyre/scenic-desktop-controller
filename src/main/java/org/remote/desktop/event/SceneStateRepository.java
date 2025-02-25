package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.model.vto.SceneVto;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static jxdotool.xDoToolUtil.getCurrentWindowTitle;

@Component
@RequiredArgsConstructor
public class SceneStateRepository implements ApplicationListener<XdoCommandEvent> {
    private final List<Consumer<String>> sceneObservers = new LinkedList<>();

    private String lastRecognized;

    public String getLastSceneNameRecorded() {
        return lastRecognized;
    }

    private SceneVto forcedScene;

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        Optional.of(event)
                .map(XdoCommandEvent::getNextScene)
                .map(q -> {
                    System.out.println("setting scene name forced: " + q.getName());

                    return forcedScene = q;
                })
                .ifPresent(q ->
                        sceneObservers.forEach(p -> p.accept((q).getName()))
                );
    }

    public String tryGetCurrentName() {
        return Optional.ofNullable(getCurrentWindowTitle())
                .map(q -> {
                    sceneObservers.forEach(p -> p.accept(q));
                    return lastRecognized = q;
                })
                .orElse(lastRecognized);
    }

    public void nullifyForcedScene() {
        forcedScene = null;
    }

    public SceneVto getForcedScene() {
        return forcedScene;
    }

    public boolean isSceneForced() {
        return Objects.nonNull(forcedScene);
    }

    public void registerSceneObserver(Consumer<String> observer) {
        sceneObservers.add(observer);
    }

}
