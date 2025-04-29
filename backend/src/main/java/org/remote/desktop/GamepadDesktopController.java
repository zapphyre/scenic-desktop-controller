package org.remote.desktop;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(
        value = {"classpath:settings.properties", "file:./settings.properties"},
        ignoreResourceNotFound = true
)
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties
public class GamepadDesktopController {

    public static void main(String[] args) {
        SpringApplication.run(GamepadDesktopController.class, args);
    }
}

