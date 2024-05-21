package com.vk.codeanalysis.core.distributor;

import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.public_interface.dto.SolutionPutRequest;
import com.vk.codeanalysis.report_dto.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributorServiceImpl implements DistributorServiceV0 {
    private final ExecutorService executor;
    private final Map<Language, TaskCollectorV0> collectors;
    private final ReportGeneratorService reportGenerator;

    @Override
    public void put(SolutionPutRequest request) {
        TaskCollectorV0 collector = collectors.get(request.lang());

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }

        executor.execute(() ->
                collector.add(
                        request.taskId(),
                        request.userId(),
                        request.solutionId(),
                        request.program()
                )
        );
    }

    @Override
    public ReportDto getReport(float thresholdStart,
                                   float thresholdEnd,
                                   Set<Long> tasksId,
                                   Set<Long> usersId,
                                   Set<String> langs) {

        if (
                thresholdStart < 0 || thresholdStart > 100
                        || thresholdEnd < 0 || thresholdEnd > 100
                        || thresholdStart > thresholdEnd
        ) {
            throw new IllegalArgumentException("Wrong similarity threshold value");
        }

        return reportGenerator.generateReport(thresholdStart, thresholdEnd, tasksId, usersId, langs);
    }
}
