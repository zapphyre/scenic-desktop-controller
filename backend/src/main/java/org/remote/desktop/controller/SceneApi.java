package org.remote.desktop.controller;

import org.springframework.web.bind.annotation.GetMapping;

public interface SceneApi {

    @GetMapping(value = "current-name", consumes = "application/json", produces = "application/json")
    String getCurrentSceneName();
}
