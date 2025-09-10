package org.remote.desktop;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.EventDao;
import org.remote.desktop.db.entity.Action;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.Mode;
import org.remote.desktop.db.repository.*;
import org.remote.desktop.model.EAdapterMode;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.service.impl.SceneService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.List;
import java.util.Optional;

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
    private final EventRepository  eventRepository;
    private final ButtonEventRepository  buttonEventRepository;
    private final SceneService sceneService;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(GamepadDesktopController.class, args);
    }

//    @PostConstruct
    void migr() {
        List<EventVto> events = sceneService.getAllSceneVtos("WINDER")
                .stream().flatMap(q -> q.getEvents().stream())
                .filter(q -> q.getActions() == null || q.getActions().isEmpty())
                .toList();

        events.stream()
                .filter(q -> q.getButtonEvent() != null)
                .map(q -> eventRepository.findById(q.getId()))
                .map(Optional::get)
                .map(Event::getButtonEvent)
                .map(q -> q.withEvent(null))
                .forEach(buttonEventRepository::delete);
        buttonEventRepository.flush();

        List<Event> list = events.stream()
                .map(q -> eventRepository.findById(q.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(q -> q.withScene(null))
                .toList();

        eventRepository.deleteAll(list);
    }

}

