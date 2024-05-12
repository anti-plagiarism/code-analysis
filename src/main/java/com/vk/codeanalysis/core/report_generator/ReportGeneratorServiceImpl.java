package com.vk.codeanalysis.core.report_generator;

import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV1;
import com.vk.codeanalysis.tokenizer.CollisionReport;
import com.vk.codeanalysis.tokenizer.PlagiarismDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private final Map<Language, TaskCollectorV1> collectors;

    @Override
    public String generate(float thresholdStart, float thresholdEnd) {
        return getReport(thresholdStart, thresholdEnd);
    }

    private String getReport(float thresholdStart, float thresholdEnd) {

        StringBuilder sb = new StringBuilder();
        sb.append("## Отчет о сравнении решений участников")
                .append("\n\n## Порог совпадения = [")
                .append(thresholdStart)
                .append(", ")
                .append(thresholdEnd)
                .append("]\n\n\n## Подробный отчет о совпадениях\n");

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

                        boolean isInInterval = checkThresholdInterval(similarity, thresholdStart, thresholdEnd);

                        if (isInInterval) {
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

    private static boolean checkThresholdInterval(float value, float start, float end) {
        return value >= start && value <= end;
    }
}
