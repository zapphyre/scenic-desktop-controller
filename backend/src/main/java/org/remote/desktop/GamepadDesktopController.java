package org.remote.desktop;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.List;

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

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(GamepadDesktopController.class, args);
    }

    private final SceneRepository  sceneRepository;
    private final XdoActionRepository  xdoActionRepository;

//    @PostConstruct
    void init() {
        List<Scene> winder = sceneRepository.findAllByMode_AdapterMode("WINDER");

        List<Event> list = winder.stream()
                .flatMap(q -> q.getEvents().stream()
                        .filter(p -> p.getActions().size() > 1)
                )
                .toList();

//        xdoActionRepository.delete(list.getFirst().getActions().getLast());
    }
}

