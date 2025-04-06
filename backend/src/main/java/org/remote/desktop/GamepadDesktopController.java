package org.remote.desktop;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.repository.SceneRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootApplication
@RequiredArgsConstructor
@PropertySource("classpath:settings.properties")
@EnableConfigurationProperties
public class GamepadDesktopController {

    private final SceneRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(GamepadDesktopController.class, args);

        // Ensure JavaFX toolkit is initialized
//        Platform.setImplicitExit(true); // Close JavaFX when the last window closes
//        JavaFxApplication.launch(JavaFxApplication.class, args);
//        Stage stage = new Stage();
//        Pane pane = new Pane();
    }
}

