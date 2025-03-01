package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.model.dto.SceneDto;
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
    private final List<Consumer<String>> recognizedSceneObservers = new LinkedList<>();
    private final List<Consumer<String>> forcedSceneObservers = new LinkedList<>();

    private String lastRecognized;

    public String getLastSceneNameRecorded() {
        return lastRecognized;
    }

    private SceneDto forcedScene;

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        Optional.of(event)
                .map(XdoCommandEvent::getNextScene)
                .map(q -> forcedScene = q)
                .ifPresent(q -> forcedSceneObservers.forEach(p -> p.accept((q).getName())));
    }

    public String tryGetCurrentName() {
        return Optional.ofNullable(getCurrentWindowTitle())
                .map(q -> {
                    recognizedSceneObservers.forEach(p -> p.accept(q));
                    return lastRecognized = q;
                })
                .orElse(lastRecognized);
    }

    public void nullifyForcedScene() {
        forcedScene = null;
    }

    public SceneDto getForcedScene() {
        return forcedScene;
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

}
