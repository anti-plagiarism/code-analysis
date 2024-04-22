package com.vk.codeanalysis.service;

import com.vk.codeanalysis.plagiarismalg.CollisionReport;
import com.vk.codeanalysis.plagiarismalg.PlagiarismDetector;
import com.vk.codeanalysis.plagiarismalg.TaskCollector;
import com.vk.codeanalysis.entity.SolutionGetRequest;
import com.vk.codeanalysis.entity.SolutionPutRequest;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
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

    private final Map<String, TaskCollector> collectors =
            Map.of(
                    "java", new TaskCollector(new TreeSitterJava()),
                    "cpp", new TaskCollector(new TreeSitterCpp()),
                    "python", new TaskCollector(new TreeSitterPython())
            );

    public void put(SolutionPutRequest request) {
        TaskCollector collector = collectors.get(request.getLang().toLowerCase());

        assert collector != null;

        executor.execute(() -> {
            try {
                collector.add(request.getTaskId(), request.getSolutionId(), request.getProgram());
            } catch (IOException e) {
                LOGGER.error("Error while file processing");
                throw new UncheckedIOException(e);
            }
        });
    }

    public String get(SolutionGetRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Отчет о сравнении решений участников")
                .append("\n\n## Порог совпадения = ")
                .append(request.getSimilarityThreshold())
                .append("\n\n\n## Подробный отчет о совпадениях\n");

        int similarityCounter = 1;
        for (Map.Entry<String, TaskCollector> collectorEntry : collectors.entrySet()) {

            sb.append("\nЯзык ").append(collectorEntry.getKey()).append("\n");
            for (Map.Entry<Long, PlagiarismDetector> detectorsEntry : collectorEntry.getValue().getDetectors().entrySet()) {

                for (Map.Entry<Long, CollisionReport> reportsEntry : detectorsEntry.getValue().getReports().entrySet()) {

                    int totalFingerprints = reportsEntry.getValue().getTotalFingerprints();
                    for (Map.Entry<Long, Integer> collisionEntry : reportsEntry.getValue().getCollisions().entrySet()) {

                        float similarity = 100 * (collisionEntry.getValue() * 1F) / totalFingerprints;

                        if (similarity >= request.getSimilarityThreshold()) {
                            sb.append("### Совпадение ")
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
