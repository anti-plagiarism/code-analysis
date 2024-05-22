package com.vk.codeanalysis.core.distributor;

import com.vk.codeanalysis.dto.request.SolutionIgnoreRequest;
import com.vk.codeanalysis.dto.request.SolutionPutRequest;
import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.dto.report.ReportDto;
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
    private final Map<Language, TaskCollectorV0> collectors;
    private final ReportGeneratorService reportGenerator;

    @Override
    public void put(SolutionPutRequest request) {
        TaskCollectorV0 collector = getCollector(request.lang());
        //TODO здесь executor генерировал исключения
        collector.add(
                request.taskId(),
                request.solutionId(),
                request.userId(),
                request.program()
        );
    }

    @Override
    public CompletableFuture<ReportDto> getGeneralReport(float thresholdStart,
                                                         float thresholdEnd,
                                                         Set<Long> tasks,
                                                         Set<Long> users,
                                                         Set<Language> langs) {
        if (isThresholdIncorrect(thresholdStart) || isThresholdIncorrect(thresholdEnd)
                || thresholdStart > thresholdEnd) {
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
                                                         Language lang,
                                                         String code) {
        TaskCollectorV0 collector = getCollector(lang);

        CompletableFuture<ReportDto> reportDto = CompletableFuture.runAsync(
                () -> collector.add(taskId, solutionId, userId, code),
                submitExecutor
        ).thenApplyAsync(
                voidResult -> reportGenerator.generatePrivateReport(taskId, solutionId, userId, lang, code),
                reportExecutor
        );

        // TODO обработать исключения

        return reportDto;
    }

    @Override
    public void addIgnored(SolutionIgnoreRequest request) {
        TaskCollectorV0 collector = getCollector(request.lang());

        // TODO проверить необходимость этих Executor
        submitExecutor.execute(() ->
                collector.addIgnored(request.taskId(), request.program())
        );
    }

    private TaskCollectorV0 getCollector(Language lang) {
        TaskCollectorV0 collector = collectors.get(lang);

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }
        return collector;
    }

    private boolean isThresholdIncorrect(float threshold) {
        return threshold < 0 || threshold > 100;
    }
}
