package com.vk.codeanalysis.core.distributor;

import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.report_dto.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributorServiceImpl implements DistributorServiceV0 {
    private final ExecutorService submitExecutor;
    private final ExecutorService reportExecutor;
    private final Map<String, TaskCollectorV0> collectors;
    private final ReportGeneratorService reportGenerator;

    @Override
    public void put(long taskId, long solutionId, long userId, String lang, String code) {
        TaskCollectorV0 collector = collectors.get(lang);

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }

        submitExecutor.execute(() ->
                collector.add(
                        taskId,
                        solutionId,
                        userId,
                        code
                )
        );
    }

    @Override
    public CompletableFuture<ReportDto> getGeneralReport(float thresholdStart,
                                                         float thresholdEnd,
                                                         Set<Long> tasks,
                                                         Set<Long> users,
                                                         Set<String> langs) {
        if (
                thresholdStart < 0 || thresholdStart > 100
                        || thresholdEnd < 0 || thresholdEnd > 100
                        || thresholdStart > thresholdEnd
        ) {
            throw new IllegalArgumentException("Wrong similarity threshold value");
        }

        return CompletableFuture.supplyAsync(
                () -> reportGenerator.generateGeneralReport(thresholdStart, thresholdEnd, tasks, users, langs),
                submitExecutor
        );
    }

    @Override
    public CompletableFuture<ReportDto> getPrivateReport(long taskId,
                                                         long solutionId,
                                                         long userId,
                                                         String lang,
                                                         String code) {
        TaskCollectorV0 collector = collectors.get(lang);

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }

        CompletableFuture<ReportDto> reportDto = CompletableFuture.runAsync(
                () -> collector.add(taskId, solutionId, userId, code),
                submitExecutor
        ).thenApplyAsync(
                voidResult -> reportGenerator
                        .generatePrivateReport(taskId, solutionId, userId, lang, code),
                reportExecutor
        );

        // TODO обработать исключения

        return reportDto;
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


}
