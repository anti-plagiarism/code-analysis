package com.vk.codeanalysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class FileTrackerConfig {

    private static final int CPU_THREADS_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_SECONDS = 3;
    private static final int MAX_CAPACITY = 300;

    @Bean
    public ExecutorService executor() {
        return new ThreadPoolExecutor(
                CPU_THREADS_COUNT,
                CPU_THREADS_COUNT,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(MAX_CAPACITY),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean
    public WatchService watchService() throws IOException {
        return FileSystems.getDefault().newWatchService();
    }

    @Bean
    public Map<WatchKey, Path> keys() {
        return new HashMap<>();
    }

}
