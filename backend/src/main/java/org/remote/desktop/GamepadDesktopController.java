package org.remote.desktop;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.ButtonEvent;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.db.repository.EventRepository;
import org.remote.desktop.db.repository.GamepadEventRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@Transactional
@PropertySource(
        value = {"classpath:settings.properties", "file:./settings.properties"},
        ignoreResourceNotFound = true
)
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties
public class GamepadDesktopController {

    final GamepadEventRepository gamepadEventRepository;
    final EventRepository eventRepository;
    final XdoActionRepository xdoActionRepository;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(GamepadDesktopController.class, args);
    }

//    @PostConstruct
    void migrate() {

        List<GamepadEvent> gEvents = gamepadEventRepository.findAll();
        xdoActionRepository.deleteAll();

        for (GamepadEvent gEvent : gEvents) {
            Event event = new Event();

            ButtonEvent buttonEvent = new ButtonEvent();
//            buttonEvent.setEvent(event);
            event.setButtonEvent(buttonEvent);

            event.setScene(gEvent.getScene());
            event.setNextScene(gEvent.getNextScene());
            buttonEvent.setTrigger(gEvent.getTrigger());
            buttonEvent.setMultiplicity(gEvent.getMultiplicity());
            buttonEvent.setLongPress(gEvent.isLongPress());

            ArrayList<XdoAction> actions = new ArrayList<>();
//            event.setActions(new ArrayList<>(gEvent.getActions()));
            for (XdoAction action : gEvent.getActions()) {

                action.setId(null);
                action.setGamepadEvent(null);
//                XdoAction save = xdoActionRepository.save(action);
                action.setEvent(event);
                actions.add(action);
//                actions.add(save);
//                xdoActionRepository.flush();
            }
            event.setActions(actions);

//            xdoActionRepository.flush();
            buttonEvent.setModifiers(new ArrayList<>(gEvent.getModifiers()));
//            gamepadEventRepository.delete(gEvent);

            eventRepository.save(event);

//            xdoActionRepository.deleteAll();
//            xdoActionRepository.flush();
            xdoActionRepository.saveAll(actions);

            eventRepository.flush();
        }

//        gamepadEventRepository.deleteAll();
//        gamepadEventRepository.flush();

    }
}

