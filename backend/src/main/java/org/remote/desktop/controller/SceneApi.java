package org.remote.desktop.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

public interface SceneApi {

    @GetMapping(value = "current-name", produces = MediaType.TEXT_PLAIN_VALUE)
    String getCurrentSceneName();
}
