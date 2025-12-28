package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.service.impl.SceneManager;
import org.remote.desktop.ui.scene.SceneReporter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SceneReporterConfig implements ApplicationListener<GpadCommandEvent> {

    private SceneReporter sceneReporter;

    @Bean
    public SceneReporter createSceneReporter(SceneManager xdoSceneService) {
        sceneReporter = new SceneReporter();

        xdoSceneService.registerForcedSceneObserver(sceneReporter::render);
        xdoSceneService.registerRecognizedSceneObserverChange(_ -> sceneReporter.hide());

        return sceneReporter;
    }

    @Override
    public void onApplicationEvent(GpadCommandEvent e) {
        if (!e.getKeyPart().getKeyEvt().equals("SCENE_RESET") ||
                sceneReporter == null)
            return;

        sceneReporter.hide();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
