package com.vk.codeanalysis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class DistributorServiceConfig {
    @Value("${executor.processors.cpu-threads-count}")
    private int cpuThreadsCount;

    @Value("${executor.keep-alive-in-seconds}")
    private int keepAliveSeconds;

    @Value("${executor.queue.max-capacity}")
    private int maxCapacity;

    @Bean
    public ExecutorService submitExecutor() {
        return new ThreadPoolExecutor(
                cpuThreadsCount,
                cpuThreadsCount,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxCapacity),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean
    public ExecutorService reportExecutor() {
        return new ThreadPoolExecutor(
                cpuThreadsCount,
                cpuThreadsCount,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxCapacity),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
