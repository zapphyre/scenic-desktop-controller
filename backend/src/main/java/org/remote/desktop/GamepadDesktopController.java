package org.remote.desktop;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.property.SettingsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@RequiredArgsConstructor
@PropertySource("classpath:settings.properties")
@EnableConfigurationProperties
public class GamepadDesktopController {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(GamepadDesktopController.class, args);
    }
}

