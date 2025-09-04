package org.remote.desktop;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Action;
import org.remote.desktop.db.entity.Mode;
import org.remote.desktop.db.repository.ModeRepository;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.remote.desktop.model.EAdapterMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.List;

import static org.remote.desktop.db.dao.SettingsDao.WINDER_SCENE_NAME;

@Transactional
@PropertySources({
        @PropertySource(
                value = {"file:./settings.properties", "classpath:settings.properties"},
                ignoreResourceNotFound = true
        )})
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties
public class GamepadDesktopController {

    private final XdoActionRepository  xdoActionRepository;
    private final SceneRepository  sceneRepository;
    private final ModeRepository  modeRepository;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(GamepadDesktopController.class, args);
    }

//    @PostConstruct
    void migr() {
        Mode mode = modeRepository.findById(1l).orElseThrow();

        sceneRepository.findAll().stream()
               .peek(q -> q.setMode(mode))
               .forEach(sceneRepository::save);
    }

}

