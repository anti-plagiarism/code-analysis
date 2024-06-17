package com.vk.codeanalysis.core.distributor;

import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.task_collector.TaskCollectorFactoryService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollector;
import com.vk.codeanalysis.public_interface.distributor.DistributorService;
import com.vk.codeanalysis.public_interface.dto.report.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.vk.codeanalysis.public_interface.utils.FileUtils.getLanguageFromExtension;
import static com.vk.codeanalysis.public_interface.utils.FileUtils.getProgram;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributorServiceImpl implements DistributorService {
    private final ExecutorService submitExecutor;
    private final ExecutorService reportExecutor;
    private final TaskCollectorFactoryService taskCollectorFactory;
    private final ReportGeneratorService reportGenerator;

    @Override
    public void put(long taskId, long solutionId, long userId, Language language, String file) {
        TaskCollector collector = taskCollectorFactory.getCollector(language);
        collector.add(taskId, solutionId, userId, file);
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
                reportExecutor
        );
    }

    @Override
    public CompletableFuture<ReportDto> getPrivateReport(long taskId,
                                                         long solutionId,
                                                         long userId,
                                                         MultipartFile file) {
        Language language = getLanguageFromExtension(file);
        TaskCollector collector = taskCollectorFactory.getCollector(language);

        return CompletableFuture.runAsync(
                () -> collector.add(taskId, solutionId, userId, getProgram(file)),
                submitExecutor
        ).thenApplyAsync(
                voidResult -> reportGenerator.generatePrivateReport(taskId, solutionId, userId, language),
                reportExecutor
        );
    }

    @Override
    public void addIgnored(long taskId, MultipartFile file) {
        Language language = getLanguageFromExtension(file);
        TaskCollector collector = taskCollectorFactory.getCollector(language);

        collector.addIgnored(taskId, getProgram(file));
    }

    private boolean isThresholdIncorrect(float threshold) {
        return threshold < 0 || threshold > 100;
    }
}
