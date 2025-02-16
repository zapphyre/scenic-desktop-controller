package org.remote.desktop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class GamepadDesktopController {

    public static void main(String[] args) {
        SpringApplication.run(GamepadDesktopController.class, args);
    }
}
