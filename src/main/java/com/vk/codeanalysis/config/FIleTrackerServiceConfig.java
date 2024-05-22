package com.vk.codeanalysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class FIleTrackerServiceConfig {
    private static final int CPU_THREADS_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_SECONDS = 3;
    private static final int MAX_CAPACITY = 1000;

    @Bean
    public ExecutorService trackerExecutor() {
        return new ThreadPoolExecutor(
                CPU_THREADS_COUNT,
                CPU_THREADS_COUNT,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(MAX_CAPACITY),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
