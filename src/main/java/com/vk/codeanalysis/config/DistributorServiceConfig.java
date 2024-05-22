package com.vk.codeanalysis.config;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import com.vk.codeanalysis.tokenizer.TaskCollectorImpl;
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
    private static final int CPU_THREADS_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_SECONDS = 3;
    private static final int MAX_CAPACITY = 1000;

    @Bean
    public ExecutorService submitExecutor() {
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
    public ExecutorService reportExecutor() {
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
    public Map<String, TaskCollectorV0> collectors() {
        return Map.of(
                Language.JAVA.getName(), new TaskCollectorImpl(new TreeSitterJava()),
                Language.CPP.getName(), new TaskCollectorImpl(new TreeSitterCpp()),
                Language.PYTHON.getName(), new TaskCollectorImpl(new TreeSitterPython())
        );
    }
}
