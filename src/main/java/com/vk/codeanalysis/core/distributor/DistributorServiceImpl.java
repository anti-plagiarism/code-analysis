package com.vk.codeanalysis.core.distributor;

import com.vk.codeanalysis.public_interface.dto.SolutionIgnoreRequest;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV1;
import com.vk.codeanalysis.tokenizer.CollisionReport;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.tokenizer.PlagiarismDetector;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.public_interface.dto.SolutionPutRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributorServiceImpl implements DistributorServiceV0 {
    private final ExecutorService executor;
    private final Map<Language, TaskCollectorV1> collectors;

    @Override
    public void put(SolutionPutRequest request) {
        TaskCollectorV1 collector = collectors.get(request.lang());

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }

        executor.execute(() ->
                collector.add(request.taskId(), request.solutionId(), request.program())
        );
    }

    @Override
    public void addIgnored(SolutionIgnoreRequest request) {
        TaskCollectorV1 collector = collectors.get(request.lang());

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }

        executor.execute(() ->
                collector.addIgnored(request.taskId(), request.program())
        );
    }

    @Override
    public String get(float similarityThreshold) {
        if (similarityThreshold < 0 || similarityThreshold > 100) {
            throw new IllegalArgumentException("Wrong similarity threshold value");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Отчет о сравнении решений участников")
                .append("\n\n## Порог совпадения = ")
                .append(similarityThreshold)
                .append("\n\n\n## Подробный отчет о совпадениях\n");

        int similarityCounter = 1;
        for (var collectorEntry : collectors.entrySet()) {
            String language = collectorEntry.getKey().getName();
            sb.append("\n### Язык ")
                    .append(language)
                    .append("\n");
            for (Map.Entry<Long, PlagiarismDetector> detectorsEntry : collectorEntry.getValue().getDetectors().entrySet()) {

                for (Map.Entry<Long, CollisionReport> reportsEntry : detectorsEntry.getValue().getReports().entrySet()) {

                    int totalFingerprints = reportsEntry.getValue().getTotalFingerprints();
                    for (Map.Entry<Long, Integer> collisionEntry : reportsEntry.getValue().getCollisions().entrySet()) {

                        float similarity = 100 * (collisionEntry.getValue() * 1F) / totalFingerprints;

                        if (similarity >= similarityThreshold) {
                            sb.append("#### Совпадение ")
                                    .append(similarityCounter++)
                                    .append("\n")
                                    .append("id1=")
                                    .append(reportsEntry.getKey())
                                    .append(" и id2=")
                                    .append(collisionEntry.getKey())
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
