package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.service.impl.SceneManager;
import org.remote.desktop.ui.scene.SceneReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SceneReporterConfig {

    @Bean
    public SceneReporter createSceneReporter(SceneManager xdoSceneService) {
        SceneReporter sceneReporter = new SceneReporter();

        xdoSceneService.registerForcedSceneObserver(sceneReporter::render);
        xdoSceneService.registerRecognizedSceneObserverChange(_ -> sceneReporter.hide());

        return sceneReporter;
    }
}
