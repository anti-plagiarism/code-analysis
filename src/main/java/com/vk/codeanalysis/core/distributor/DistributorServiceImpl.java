package com.vk.codeanalysis.core.distributor;

import com.vk.codeanalysis.public_interface.report_generator.ReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.dto.report.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.vk.codeanalysis.Utils.PathUtils.getLanguageFromExtension;
import static com.vk.codeanalysis.Utils.PathUtils.getProgram;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributorServiceImpl implements DistributorServiceV0 {
    private final ExecutorService submitExecutor;
    private final ExecutorService reportExecutor;
    private final Map<Language, TaskCollectorV0> collectors;
    private final ReportGeneratorService reportGenerator;

    @Override
    public void put(Long taskId, Long solutionId, Long userId, Language language, String file) {
        TaskCollectorV0 collector = getCollector(language);
        collector.add(taskId, solutionId, userId, file);
    }

    @Override
    public CompletableFuture<ReportDto> getGeneralReport(float thresholdStart,
                                                         float thresholdEnd,
                                                         Set<Long> tasks,
                                                         Set<Long> users,
                                                         Set<Language> langs) {
        if (thresholdStart > thresholdEnd) {
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
        TaskCollectorV0 collector = getCollector(language);

        return CompletableFuture.runAsync(
                () -> collector.add(taskId, solutionId, userId, getProgram(file)),
                submitExecutor
        ).thenApplyAsync(
                voidResult -> reportGenerator.generatePrivateReport(taskId, solutionId, userId, language),
                reportExecutor
        );
        // TODO обработать исключения
    }

    @Override
    public void addIgnored(Long taskId, MultipartFile file) {
        Language language = getLanguageFromExtension(file);
        TaskCollectorV0 collector = getCollector(language);

        collector.addIgnored(taskId, getProgram(file));
    }

    private TaskCollectorV0 getCollector(Language lang) {
        TaskCollectorV0 collector = collectors.get(lang);

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }
        return collector;
    }
}
