package com.vk.codeanalysis.config;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import com.vk.codeanalysis.tokenizer.TaskCollectorImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.treesitter.TreeSitterCpp;
import org.treesitter.TreeSitterJava;
import org.treesitter.TreeSitterPython;

import java.util.Map;
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

    @Bean
    public Map<Language, TaskCollectorV0> collectors() {
        return Map.of(
                Language.JAVA, new TaskCollectorImpl(new TreeSitterJava()),
                Language.CPP, new TaskCollectorImpl(new TreeSitterCpp()),
                Language.PYTHON, new TaskCollectorImpl(new TreeSitterPython())
        );
    }
}
