package org.remote.desktop;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

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

}

