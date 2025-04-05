package org.remote.desktop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ThreadConfig {

    @Bean
    public ThreadFactory threadFactory() {
        CustomizableThreadFactory factory = new CustomizableThreadFactory("JPAD-CONNECTOR");
        factory.setDaemon(true); // Optional
        factory.setThreadPriority(Thread.MAX_PRIORITY); // Optional
        factory.setThreadGroupName("JPAD-CONNECTOR");
        return factory;
    }

    @Bean
    public ScheduledExecutorService executor(ThreadFactory threadFactory) {
        return Executors.newScheduledThreadPool(10, threadFactory);
    }
}