package com.vk.codeanalysis.core.report_generator;

import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import com.vk.codeanalysis.tokenizer.CollisionReport;
import com.vk.codeanalysis.tokenizer.PlagiarismDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private final Map<Language, TaskCollectorV0> collectors;

    @Override
    public String generate(float thresholdStart, float thresholdEnd) {

        StringBuilder sb = new StringBuilder();
        sb.append("### Отчет о сравнении решений участников")
                .append("\n\n#### Порог совпадения = [")
                .append(thresholdStart)
                .append(", ")
                .append(thresholdEnd)
                .append("]\n");

        int similarityCounter = 1;
        for (var collectorEntry : collectors.entrySet()) {

            String language = collectorEntry.getKey().getName();
            sb.append("\n#### Язык ").append(language).append("\n");

            for (Map.Entry<Long, PlagiarismDetector> detectorsEntry : collectorEntry.getValue().getDetectors().entrySet()) {

                Map<Long, List<Long>> submittedSolutions = detectorsEntry.getValue().getSubmittedSolutions();
                Map<Long, Long> solutionToUser = detectorsEntry.getValue().getSolutionToUser();

                sb.append("\n#### TaskId = ").append(detectorsEntry.getKey()).append("\n");

                for (Map.Entry<Long, CollisionReport> reportsEntry : detectorsEntry.getValue().getReports().entrySet()) {

                    long baseId = reportsEntry.getKey();

                    long userBaseSolution = solutionToUser.get(baseId);
                    long lastSolutionId = submittedSolutions.get(userBaseSolution).getLast();

                    if (baseId != lastSolutionId) {
                        continue;
                    }

                    int totalFingerprints = reportsEntry.getValue().getTotalFingerprints();
                    for (Map.Entry<Long, Integer> collisionEntry : reportsEntry.getValue().getCollisions().entrySet()) {

                        long currId = collisionEntry.getKey();

                        long userCurrSolution = solutionToUser.get(currId);
                        lastSolutionId = submittedSolutions.get(userCurrSolution).getLast();

                        if (currId != lastSolutionId) {
                            continue;
                        }

                        float similarity = 100 * (collisionEntry.getValue() * 1F) / totalFingerprints;
                        boolean isInInterval = checkThresholdInterval(similarity, thresholdStart, thresholdEnd);

                        if (isInInterval) {
                            sb.append("##### Совпадение ")
                                    .append(similarityCounter++)
                                    .append("\n")
                                    .append("UserId=")
                                    .append(baseId)
                                    .append(" - SolutionId=")
                                    .append(userBaseSolution)
                                    .append(" <--> UserId=")
                                    .append(currId)
                                    .append(" - SolutionId=")
                                    .append(userCurrSolution)
                                    .append("\n\t - Процент совпадений = ")
                                    .append(similarity)
                                    .append("\n\n");
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
