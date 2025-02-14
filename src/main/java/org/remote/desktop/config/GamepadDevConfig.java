package org.remote.desktop.config;

import org.asmus.facade.TimedButtonGamepadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GamepadDevConfig {

    private final TimedButtonGamepadFactory timedButtonGamepadFactory = new TimedButtonGamepadFactory();
    private final List<Runnable> factoryDisposable = timedButtonGamepadFactory.watchForDevices(0, 1);

    @Bean
    public TimedButtonGamepadFactory createTimedButtonGamepadFactory() {
        return timedButtonGamepadFactory;
    }
}
