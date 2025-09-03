package org.remote.desktop;

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
        Mode.ModeBuilder modeBuilder = Mode.builder().adapterMode(EAdapterMode.WINDER);

        List<Action> actions = sceneRepository.findAll()
                .stream().filter(
                        q -> q.getName().equalsIgnoreCase(WINDER_SCENE_NAME)
                )
                .flatMap(q -> q.getEvents().stream())
                .flatMap(q -> q.getActions().stream())
                .toList();

        Mode mode = modeBuilder.build();
        Mode saved = modeRepository.save(mode);

        actions.forEach(q -> q.setMode(saved));
//        saved = modeRepository.save(mode);

        xdoActionRepository.saveAll(actions);
    }

}

