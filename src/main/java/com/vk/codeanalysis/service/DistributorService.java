package com.vk.codeanalysis.service;

import com.vk.codeanalysis.plagiarismalg.CollisionReport;
import com.vk.codeanalysis.plagiarismalg.Language;
import com.vk.codeanalysis.plagiarismalg.PlagiarismDetector;
import com.vk.codeanalysis.plagiarismalg.TaskCollector;
import com.vk.codeanalysis.entity.SolutionGetRequest;
import com.vk.codeanalysis.entity.SolutionPutRequest;
import com.vk.codeanalysis.utils.LanguageUtil;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.treesitter.TreeSitterCpp;
import org.treesitter.TreeSitterJava;
import org.treesitter.TreeSitterPython;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class DistributorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributorService.class);
    private static final int CPU_THREADS_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int KEEPALIVE_MILLIS = 3000;

    private final ExecutorService executor = new ThreadPoolExecutor(
            CPU_THREADS_COUNT,
            CPU_THREADS_COUNT,
            KEEPALIVE_MILLIS,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(300),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private final Map<Language, TaskCollector> collectors =
            Map.of(
                    Language.JAVA, new TaskCollector(new TreeSitterJava()),
                    Language.CPP, new TaskCollector(new TreeSitterCpp()),
                    Language.PYTHON, new TaskCollector(new TreeSitterPython())
            );

    public void put(SolutionPutRequest request) throws BadRequestException {
        TaskCollector collector = collectors.get(
                LanguageUtil.fromString(
                        request.lang().toLowerCase()));

        if (collector == null) {
            throw new BadRequestException("Unsupported language");
        }

        executor.execute(() -> {
            try {
                collector.add(request.taskId(), request.solutionId(), request.program());
            } catch (IOException e) {
                LOGGER.error("Error while file processing");
                throw new UncheckedIOException(e);
            }
        });
    }

    public String get(SolutionGetRequest request) throws BadRequestException {
        float similarityThreshold = request.similarityThreshold();
        if (similarityThreshold < 0 || similarityThreshold > 100) {
            throw new BadRequestException("Wrong similarity threshold value");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Отчет о сравнении решений участников")
                .append("\n\n## Порог совпадения = ")
                .append(request.similarityThreshold())
                .append("\n\n\n## Подробный отчет о совпадениях\n");

        int similarityCounter = 1;
        for (Map.Entry<Language, TaskCollector> collectorEntry : collectors.entrySet()) {

            String language = LanguageUtil.toString(collectorEntry.getKey());
            sb.append("\n### Язык ").append(language).append("\n");
            for (Map.Entry<Long, PlagiarismDetector> detectorsEntry : collectorEntry.getValue().getDetectors().entrySet()) {

                for (Map.Entry<Long, CollisionReport> reportsEntry : detectorsEntry.getValue().getReports().entrySet()) {

                    int totalFingerprints = reportsEntry.getValue().getTotalFingerprints();
                    for (Map.Entry<Long, Integer> collisionEntry : reportsEntry.getValue().getCollisions().entrySet()) {

                        float similarity = 100 * (collisionEntry.getValue() * 1F) / totalFingerprints;

                        if (similarity >= request.similarityThreshold()) {
                            sb.append("#### Совпадение ")
                                    .append(similarityCounter++).append("\n")
                                    .append("id1=").append(reportsEntry.getKey())
                                    .append(" и id2=").append(collisionEntry.getKey())
                                    .append(" - процент совпадений = ")
                                    .append(similarity)
                                    .append("\n");
                        }
                    }
                }
            }
        }

        return sb.toString();
    }
}
