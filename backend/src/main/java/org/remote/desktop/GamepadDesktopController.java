package org.remote.desktop;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.ButtonEvent;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.repository.GamepadEventRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

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

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(GamepadDesktopController.class, args);
    }


    void migrate() {

        List<GamepadEvent> gEvents = gamepadEventRepository.findAll();

        for (GamepadEvent gEvent : gEvents) {
            Event event = new Event();

            ButtonEvent buttonEvent = new ButtonEvent();
            buttonEvent.setEvent(event);
            event.setButtonEvent(buttonEvent);
            buttonEvent.setTrigger(gEvent.getTrigger());
            buttonEvent.setMultiplicity(gEvent.getMultiplicity());
            buttonEvent.setModifiers(gEvent.getModifiers());
            buttonEvent.setLongPress(gEvent.isLongPress());

            gEvent.getActions().forEach(action -> {
                action.setEvent(event);
            });
        }
    }
}

