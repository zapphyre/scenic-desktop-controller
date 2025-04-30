package org.remote.desktop.provider.impl;

import jxdotool.xDoToolUtil;
import org.remote.desktop.provider.XdoSceneProvider;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class LocalXdoSceneProvider implements XdoSceneProvider {

    private String lastRecognized = "";

    @NonNull
    @Override
    public String tryGetCurrentName() {
        String s = Optional.ofNullable(safeIdentityGet())
                .map(q -> lastRecognized = q)
                .orElse(lastRecognized);

        System.out.println("scene: " + s);

        return s;
    }

    String safeIdentityGet() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(xDoToolUtil::runIdentityScript)
                .completeOnTimeout(lastRecognized, 690, TimeUnit.MILLISECONDS);

        try {
            return future.get(420, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            System.out.println("script timed out");
            return lastRecognized;
        }
    }
}
