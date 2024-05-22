package com.vk.codeanalysis.core.report_generator;

import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import com.vk.codeanalysis.report_dto.SimilarityIntervalDto;
import com.vk.codeanalysis.report_dto.ReportDto;
import com.vk.codeanalysis.report_dto.SimilarityDto;
import com.vk.codeanalysis.tokenizer.CollisionReport;
import com.vk.codeanalysis.tokenizer.PlagiarismDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private final Map<String, TaskCollectorV0> collectors;

    @Override
    public ReportDto generateGeneralReport(
            float thresholdStart,
            float thresholdEnd,
            Set<Long> tasks,
            Set<Long> users,
            Set<String> langs
    ) {
        ReportDto reportDto = new ReportDto();
        Map<String, List<SimilarityDto>> bodyMap = new HashMap<>();
        for (var collectorEntry : collectors.entrySet()) {
            String language = collectorEntry.getKey();

            if (langs != null && !langs.contains(language)) {
                continue;
            }

            List<SimilarityDto> similarityList = new ArrayList<>();
            for (Map.Entry<Long, PlagiarismDetector> detectorsEntry : collectorEntry.getValue().getDetectors().entrySet()) {
                Map<Long, List<Long>> submittedSolutions = detectorsEntry.getValue().getSubmittedSolutions();
                Map<Long, Long> solutionToUser = detectorsEntry.getValue().getSolutionToUser();
                long taskId = detectorsEntry.getKey();

                if (tasks != null && !tasks.contains(taskId)) {
                    continue;
                }

                for (Map.Entry<Long, CollisionReport> reportsEntry : detectorsEntry.getValue().getReports().entrySet()) {
                    long baseSolutionId = reportsEntry.getKey();
                    long userBaseId = solutionToUser.get(baseSolutionId);
                    long lastSolutionId = submittedSolutions.get(userBaseId).getLast();

                    // Ignore the self-intersection.
                    if (baseSolutionId != lastSolutionId) {
                        continue;
                    }

                    int totalFingerprints = reportsEntry.getValue().getTotalFingerprints();
                    for (Map.Entry<Long, Integer> collisionEntry : reportsEntry.getValue().getCollisions().entrySet()) {
                        long currSolutionId = collisionEntry.getKey();
                        long userCurrId = solutionToUser.get(currSolutionId);

                        if (users != null
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
                            similarityList.add(
                                    new SimilarityDto(
                                            taskId,
                                            userBaseId,
                                            baseSolutionId,
                                            userCurrId,
                                            currSolutionId,
                                            similarity
                                    )
                            );
                        }
                    }
                }
            }
            bodyMap.put(language, similarityList);
        }

        reportDto.setInterval(new SimilarityIntervalDto(thresholdStart, thresholdEnd));
        reportDto.setTasks(tasks);
        reportDto.setUsers(users);
        reportDto.setLanguages(langs);
        reportDto.setBody(bodyMap);

        return reportDto;
    }

    @Override
    public ReportDto generatePrivateReport(long taskId, long solutionId, long userId, String lang, String code) {
        // TODO
        return null;
    }

    private static boolean checkThresholdInterval(float value, float start, float end) {
        return value >= start && value <= end;
    }

}
