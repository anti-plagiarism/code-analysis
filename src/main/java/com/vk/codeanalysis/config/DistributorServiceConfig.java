package com.vk.codeanalysis.config;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV1;
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
    public Map<Language, TaskCollectorV1> collectors() {
        return Map.of(
                Language.JAVA, new TaskCollectorImpl(new TreeSitterJava()),
                Language.CPP, new TaskCollectorImpl(new TreeSitterCpp()),
                Language.PYTHON, new TaskCollectorImpl(new TreeSitterPython())
        );
    }
}
