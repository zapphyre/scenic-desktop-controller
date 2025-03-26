package org.remote.desktop.config;

import jakarta.annotation.PreDestroy;
import org.asmus.builder.EventProducer;
import org.asmus.builder.IntrospectedEventFactory;
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

    @Bean
    public IntrospectedEventFactory introspectedEventFactory() {
        return new IntrospectedEventFactory();
    }

    @PreDestroy
    void cleanup() {
        factoryDisposable.forEach(Runnable::run);
    }
}
