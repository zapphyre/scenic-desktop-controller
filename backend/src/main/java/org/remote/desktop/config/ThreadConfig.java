package org.remote.desktop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ThreadConfig {

    @Bean
    public ThreadFactory threadFactory() {
        CustomizableThreadFactory factory = new CustomizableThreadFactory("JPAD-CONNECTOR");
//        factory.setDaemon(true); // Optional
//        factory.setThreadPriority(Thread.MAX_PRIORITY); // Optional
        factory.setThreadGroupName("JPAD-CONNECTOR");
        return factory;
    }


    @Primary
    @Bean(name = "identityTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(120); // Set desired pool size (e.g., 4 threads)
        executor.setMaxPoolSize(160); // Maximum pool size
        executor.setQueueCapacity(100); // Queue capacity for tasks
        executor.setThreadNamePrefix("Identity-Thread-"); // For easier debugging
        executor.setWaitForTasksToCompleteOnShutdown(false); // Graceful shutdown
        executor.initialize();
        return executor;
    }

    @Bean(name = "identityScheduler")
    public Scheduler identityScheduler(ThreadPoolTaskExecutor identityTaskExecutor) {
        return Schedulers.fromExecutor(identityTaskExecutor);
    }

    @Bean
    public ScheduledExecutorService executor(ThreadFactory threadFactory) {
        return Executors.newScheduledThreadPool(10, threadFactory);
    }
}