package org.remote.desktop;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Transactional
@SpringBootApplication
@RequiredArgsConstructor
@PropertySource("classpath:settings.properties")
@EnableConfigurationProperties
public class GamepadDesktopController {

    private final XdoActionRepository repository;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(GamepadDesktopController.class, args);
    }

//    @PostConstruct
//    void init() {
//        List<XdoAction> all = repository.findAll();
//        for (XdoAction action : all) {
//            String keyPress = action.getKeyPress();
//            if (keyPress == null) {
//                System.out.println("Key press is null:" + keyPress);
//                continue;
//            }
//            if (keyPress.contains("+"))
//                action.setKeyStrokes(Arrays.asList(keyPress.split("\\+")));
//            else
//                action.setKeyStrokes(List.of(keyPress));
//        }
//
//        repository.saveAll(all);
//    }
}

