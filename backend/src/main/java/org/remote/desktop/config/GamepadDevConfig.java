package org.remote.desktop.config;

import jakarta.annotation.PreDestroy;
import org.asmus.builder.EventProducer;
import org.asmus.service.JoyWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GamepadDevConfig {

    private final EventProducer eventProducer = new EventProducer();
    private final List<Runnable> factoryDisposable = eventProducer.watchForDevices(0, 1);

    @Bean
    public JoyWorker createTimedButtonGamepadFactory() {
        return eventProducer.getWorker();
    }

    @PreDestroy
    void cleanup() {
        factoryDisposable.forEach(Runnable::run);
    }
}
