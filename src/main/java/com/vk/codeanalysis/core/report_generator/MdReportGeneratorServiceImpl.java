package com.vk.codeanalysis.core.report_generator;

import com.vk.codeanalysis.public_interface.dto.report.BaseSolutionDto;
import com.vk.codeanalysis.public_interface.dto.report.TaskDto;
import com.vk.codeanalysis.public_interface.dto.report.DependentSolutionDto;
import com.vk.codeanalysis.public_interface.dto.report.ReportDto;
import com.vk.codeanalysis.public_interface.dto.report.SimilarityIntervalDto;
import com.vk.codeanalysis.public_interface.file_tracker.FileTrackerService;
import com.vk.codeanalysis.public_interface.report_generator.MdReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MdReportGeneratorServiceImpl implements MdReportGeneratorService {
    private final FileTrackerService fileTrackerService;

    @Override
    public String convertToMarkdown(ReportDto report) {
        StringBuilder mdBuilder = new StringBuilder();
        appendHeader(mdBuilder, "Отчёт по решениям");
        SimilarityIntervalDto interval = report.getInterval();
        appendInterval(mdBuilder, interval);

        Map<Language, List<TaskDto>> body = report.getBody();
        body.forEach((language, tasks) -> {
            appendLanguageSection(mdBuilder, language);
            tasks.forEach(task -> {
                long taskId = task.taskId();
                mdBuilder.append("### Задание (ID: ").append(taskId).append(")\n");
                task.baseSolutions().forEach(
                        baseSolution -> {
                            mdBuilder.append("#### Решение:\n");
                            appendBaseTask(mdBuilder, taskId, baseSolution, language);
                            mdBuilder.append("#### Сходство с другими:\n");
                            baseSolution.dependentSolutions().forEach(
                                    dependentSolution -> appendDependentTask(mdBuilder, taskId, dependentSolution, language)
                            );
                        }
                );
                mdBuilder.append("\n");
            });
        });

        return mdBuilder.toString();
    }

    @Override
    public String convertToMarkdownPrivate(ReportDto report, MultipartFile file) {
        StringBuilder mdBuilder = new StringBuilder();
        appendHeader(mdBuilder, "Проверка отдельного решения");

        Map<Language, List<TaskDto>> body = report.getBody();
        body.forEach((language, tasks) -> {
            appendLanguageSection(mdBuilder, language);
            TaskDto task = tasks.getFirst();
            long taskId = task.taskId();

            BaseSolutionDto baseSolution = task.baseSolutions().getFirst();
            mdBuilder.append("### Задание (ID: ").append(taskId).append(")\n")
                    .append("#### Решение:\n")
                    .append("- ID пользователя: ").append(baseSolution.userId()).append("\n")
                    .append("- ID решения: ").append(baseSolution.solutionId()).append("\n");

            appendSourceCode(mdBuilder, FileUtils.getProgram(file));

            mdBuilder.append("#### Сходство с другими:\n");
            baseSolution.dependentSolutions().forEach(
                    dependentSolution -> appendDependentTask(mdBuilder, taskId, dependentSolution, language)
            );
            mdBuilder.append("\n");
        });

        return mdBuilder.toString();
    }

    private void appendHeader(StringBuilder mdBuilder, String title) {
        mdBuilder.append("# ").append(title).append("\n\n");
    }

    private void appendInterval(StringBuilder mdBuilder, SimilarityIntervalDto interval) {
        mdBuilder.append("## Интервал схожести решений: [")
                .append(interval.start()).append(": ")
                .append(interval.end()).append("]\n\n");
    }

    private void appendLanguageSection(StringBuilder mdBuilder, Language language) {
        mdBuilder.append("## Язык программирования: ").append(language).append("\n\n");
    }

    private void appendBaseTask(
            StringBuilder mdBuilder,
            long taskId,
            BaseSolutionDto task,
            Language language) {

        mdBuilder.append("- ID пользователя: ").append(task.userId()).append("\n")
                .append("- ID решения: ").append(task.solutionId()).append("\n");

        String sourceCode = fetchSourceCode(taskId, task.userId(), task.solutionId(), language);
        appendSourceCode(mdBuilder, sourceCode);
    }

    private void appendDependentTask(
            StringBuilder mdBuilder,
            long taskId,
            DependentSolutionDto dependent,
            Language language) {

        mdBuilder.append("- ID пользователя: ").append(dependent.userId()).append("\n")
                .append("- ID решения: ").append(dependent.solutionId()).append("\n")
                .append("- Процент совпадений: ").append(dependent.matchesPercentage()).append("\n");

        String dependentSourceCode = fetchSourceCode(taskId, dependent.userId(), dependent.solutionId(), language);
        appendSourceCode(mdBuilder, dependentSourceCode);
        mdBuilder.append("\n");
    }

    private void appendSourceCode(StringBuilder mdBuilder, String sourceCode) {
        mdBuilder.append("<details>\n")
                .append("<summary>Исходный код</summary>\n")
                .append("<pre>\n\n")
                .append(sourceCode)
                .append("\n</pre>\n")
                .append("</details>\n\n");
    }

    private String fetchSourceCode(long taskId, long userId, long solutionId, Language language) {
        return fileTrackerService.fetchSolutionContent(
                        taskId,
                        userId,
                        solutionId,
                        language)
                .orElse("Исходный код не представлен");
    }
}



