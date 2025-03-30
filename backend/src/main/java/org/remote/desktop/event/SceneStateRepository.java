package org.remote.desktop.event;

import jxdotool.xDoToolUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static jxdotool.xDoToolUtil.getCurrentWindowTitle;
import static jxdotool.xDoToolUtil.runIdentityScript;

@Component
@RequiredArgsConstructor
public class SceneStateRepository implements ApplicationListener<XdoCommandEvent> {
    private final List<Consumer<String>> recognizedSceneObservers = new LinkedList<>();
    private final List<Consumer<String>> forcedSceneObservers = new LinkedList<>();

    private String lastRecognized;

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
        return Optional.ofNullable(safeIdentityGet())
                .map(q -> {
                    recognizedSceneObservers.forEach(p -> p.accept(q));
                    return lastRecognized = q;
                })
                .orElse(lastRecognized);
    }

    @SneakyThrows
    String safeIdentityGet() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(xDoToolUtil::runIdentityScript)
                .completeOnTimeout(lastRecognized, 21, TimeUnit.MILLISECONDS);

        try {
            // Block for at most 12ms total
            return future.get(21, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted");
            return lastRecognized;
        } catch (ExecutionException e) {
            System.err.println("Script failed: " + e.getCause());
            return lastRecognized;
        } catch (TimeoutException e) {
            // Rare case: future didn't complete even with completeOnTimeout
            System.out.println("Script timed out: " + e.getCause());
            return lastRecognized;
        }
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

}
