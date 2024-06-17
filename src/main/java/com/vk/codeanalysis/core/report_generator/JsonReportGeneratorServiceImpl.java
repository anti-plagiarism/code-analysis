package com.vk.codeanalysis.core.report_generator;

import com.vk.codeanalysis.public_interface.dto.report.BaseSolutionDto;
import com.vk.codeanalysis.public_interface.dto.report.DependentSolutionDto;
import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.task_collector.TaskCollectorFactoryService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollector;
import com.vk.codeanalysis.public_interface.dto.report.SimilarityIntervalDto;
import com.vk.codeanalysis.public_interface.dto.report.ReportDto;
import com.vk.codeanalysis.public_interface.dto.report.TaskDto;
import com.vk.codeanalysis.tokenizer.CollisionReport;
import com.vk.codeanalysis.tokenizer.PlagiarismDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class JsonReportGeneratorServiceImpl implements ReportGeneratorService {
    private final TaskCollectorFactoryService taskCollectorFactory;

    @Override
    public ReportDto generateGeneralReport(
            float thresholdStart,
            float thresholdEnd,
            Set<Long> tasks,
            Set<Long> users,
            Set<Language> langs
    ) {
        Map<Language, List<TaskDto>> bodyMap = new HashMap<>();
        for (var collectorEntry : taskCollectorFactory.getTaskCollectors().entrySet()) {
            Language language = collectorEntry.getKey();

            if (langs != null && !langs.isEmpty() && !langs.contains(language)) {
                continue;
            }

            List<TaskDto> baseTasks = new ArrayList<>();
            for (Map.Entry<Long, PlagiarismDetector> detectorsEntry : collectorEntry.getValue().getDetectors().entrySet()) {
                Map<Long, List<Long>> submittedSolutions = detectorsEntry.getValue().getSubmittedSolutions();
                Map<Long, Long> solutionToUser = detectorsEntry.getValue().getSolutionToUser();
                long taskId = detectorsEntry.getKey();

                if (tasks != null && !tasks.isEmpty() && !tasks.contains(taskId)) {
                    continue;
                }

                List<BaseSolutionDto> baseSolutions = new ArrayList<>();
                for (Map.Entry<Long, CollisionReport> reportsEntry : detectorsEntry.getValue().getReports().entrySet()) {
                    long baseSolutionId = reportsEntry.getKey();
                    long userBaseId = solutionToUser.get(baseSolutionId);
                    long lastSolutionId = submittedSolutions.get(userBaseId).getLast();

                    // Ignore the self-intersection.
                    if (baseSolutionId != lastSolutionId) {
                        continue;
                    }

                    List<DependentSolutionDto> dependentSolutions = new ArrayList<>();
                    int totalFingerprints = reportsEntry.getValue().getTotalFingerprints();
                    for (Map.Entry<Long, Integer> collisionEntry : reportsEntry.getValue().getCollisions().entrySet()) {
                        long currSolutionId = collisionEntry.getKey();
                        long userCurrId = solutionToUser.get(currSolutionId);

                        if (users != null && !users.isEmpty()
                                && (!users.contains(userBaseId) || !users.contains(userCurrId))) {
                            continue;
                        }

                        lastSolutionId = submittedSolutions.get(userCurrId).getLast();
                        if (currSolutionId != lastSolutionId) {
                            continue;
                        }
                        float similarity = 100 * (collisionEntry.getValue() * 1F) / totalFingerprints;
                        boolean isInInterval = checkThresholdInterval(similarity, thresholdStart, thresholdEnd);
                        if (isInInterval) {
                            dependentSolutions.add(
                                    DependentSolutionDto.builder()
                                            .userId(userCurrId)
                                            .solutionId(currSolutionId)
                                            .matchesPercentage(similarity)
                                            .build()
                            );
                        }
                    }
                    if (!dependentSolutions.isEmpty()) {
                        baseSolutions.add(
                                BaseSolutionDto.builder()
                                        .userId(userBaseId)
                                        .solutionId(baseSolutionId)
                                        .dependentSolutions(dependentSolutions)
                                        .build()
                        );
                    }
                }
                if (!baseSolutions.isEmpty()) {
                    baseTasks.add(
                            TaskDto.builder()
                                    .taskId(taskId)
                                    .baseSolutions(baseSolutions)
                                    .build()
                    );
                }
            }
            if (!baseTasks.isEmpty()) {
                bodyMap.put(language, baseTasks);
            }
        }

        return ReportDto.builder()
                .interval(new SimilarityIntervalDto(thresholdStart, thresholdEnd))
                .tasks(tasks)
                .users(users)
                .languages(langs)
                .body(bodyMap)
                .build();
    }

    @Override
    public ReportDto generatePrivateReport(long taskId, long solutionId, long userId, Language lang) {
        Map<Language, List<TaskDto>> bodyMap = new HashMap<>();
        List<DependentSolutionDto> dependentSolutions = new ArrayList<>();

        TaskCollector collector = taskCollectorFactory.getCollector(lang);
        PlagiarismDetector detector = collector.getDetectors().get(taskId);

        Map<Long, List<Long>> submittedSolutions = detector.getSubmittedSolutions();
        Map<Long, Long> solutionToUser = detector.getSolutionToUser();

        CollisionReport report = detector.getReports().get(solutionId);
        int totalFingerprints = report.getTotalFingerprints();

        for (var collisionEntry : report.getCollisions().entrySet()) {
            long currSolutionId = collisionEntry.getKey();
            long userCurrId = solutionToUser.get(currSolutionId);

            if (submittedSolutions.get(userCurrId).getLast() != currSolutionId) {
                // Игнорируем непоследнее решение.
                continue;
            }

            float similarity = 100 * (collisionEntry.getValue() * 1F) / totalFingerprints;

            dependentSolutions.add(
                    DependentSolutionDto.builder()
                            .userId(userCurrId)
                            .solutionId(currSolutionId)
                            .matchesPercentage(similarity)
                            .build()
            );
        }

        if (!dependentSolutions.isEmpty()) {
            bodyMap.put(
                    lang,
                    Collections.singletonList(
                            TaskDto.builder()
                                    .taskId(taskId)
                                    .baseSolutions(
                                            Collections.singletonList(
                                                    BaseSolutionDto.builder()
                                                            .userId(userId)
                                                            .solutionId(solutionId)
                                                            .dependentSolutions(dependentSolutions)
                                                            .build()
                                            )
                                    )
                                    .build()
                    )
            );
        }


        return ReportDto.builder()
                .tasks(Collections.singleton(taskId))
                .users(Collections.singleton(userId))
                .languages(Collections.singleton(lang))
                .body(bodyMap)
                .build();
    }

    private static boolean checkThresholdInterval(float value, float start, float end) {
        return value >= start && value <= end;
    }
}
