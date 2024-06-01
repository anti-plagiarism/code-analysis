package com.vk.codeanalysis.core.report_generator;

import com.vk.codeanalysis.public_interface.dto.report.BaseTaskDto;
import com.vk.codeanalysis.public_interface.dto.report.DependentTaskDto;
import com.vk.codeanalysis.public_interface.dto.report.ReportDto;
import com.vk.codeanalysis.public_interface.dto.report.SimilarityIntervalDto;
import com.vk.codeanalysis.public_interface.file_tracker.FileTrackerService;
import com.vk.codeanalysis.public_interface.report_generator.MdReportGeneratorService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        Map<Language, List<BaseTaskDto>> body = report.getBody();
        body.forEach((language, tasks) -> {
            appendLanguageSection(mdBuilder, language);
            tasks.forEach(task -> appendTaskSection(mdBuilder, task, language));
        });

        return mdBuilder.toString();
    }

    @Override
    public String convertToMarkdownPrivate(ReportDto report) {
        StringBuilder mdBuilder = new StringBuilder();
        appendHeader(mdBuilder, "Проверка отдельного решения");

        Map<Language, List<BaseTaskDto>> body = report.getBody();
        body.forEach((language, tasks) -> {
            appendLanguageSection(mdBuilder, language);
            tasks.forEach(task -> appendTaskSectionPrivate(mdBuilder, task, language));
        });

        return mdBuilder.toString();
    }

    // Вспомогательный метод для добавления заголовка
    private void appendHeader(StringBuilder mdBuilder, String title) {
        mdBuilder.append("# ").append(title).append("\n\n");
    }

    // Вспомогательный метод для добавления информации об интервале схожести
    private void appendInterval(StringBuilder mdBuilder, SimilarityIntervalDto interval) {
        mdBuilder.append("## Интервал схожести решений\n")
                .append("Старт: ").append(interval.start()).append("\n")
                .append("Конец: ").append(interval.end()).append("\n\n");
    }

    private void appendLanguageSection(StringBuilder mdBuilder, Language language) {
        mdBuilder.append("## Язык программирования: ").append(language).append("\n\n");
    }

    private void appendTaskSection(StringBuilder mdBuilder, BaseTaskDto task, Language language) {
        mdBuilder.append("### Задание ID: ").append(task.taskId()).append("\n")
                .append("ID пользователя (источник): ").append(task.userId()).append("\n")
                .append("ID решения (источник): ").append(task.solutionId()).append("\n")
                .append("#### Исходный код решения:\n\n");

        String sourceCode = fetchSourceCode(task, language);
        appendSourceCode(mdBuilder, sourceCode);

        mdBuilder.append("#### Сходства с другими:\n");
        task.dependentTasks().forEach(
                dependent -> appendDependentTask(mdBuilder, task, dependent, language, false)
        );
        mdBuilder.append("\n");
    }

    private void appendTaskSectionPrivate(StringBuilder mdBuilder, BaseTaskDto task, Language language) {
        mdBuilder.append("### Задание ID: ").append(task.taskId()).append("\n")
                .append("ID пользователя (источник): ").append(task.userId()).append("\n")
                .append("ID решения (источник): ").append(task.solutionId()).append("\n\n");

        mdBuilder.append("#### Сходства с другими:\n");
        task.dependentTasks().forEach(
                dependent -> appendDependentTask(mdBuilder, task, dependent, language, true)
        );
        mdBuilder.append("\n");
    }

    private String fetchSourceCode(BaseTaskDto task, Language language) {
        return fileTrackerService.fetchSolutionContent(task.taskId(), task.userId(), task.solutionId(), language)
                .orElse("Исходный код не представлен");
    }

    private void appendSourceCode(StringBuilder mdBuilder, String sourceCode) {
        mdBuilder.append("<details>\n")
                .append("<summary>Исходный код</summary>\n")
                .append("<pre>\n\n")
                .append(sourceCode).append("\n")
                .append("</pre>\n")
                .append("</details>\n\n");
    }

    private void appendDependentTask(
            StringBuilder mdBuilder,
            BaseTaskDto task,
            DependentTaskDto dependent,
            Language language,
            boolean addSourceCode) {
        mdBuilder.append("- ID пользователя (цель): ").append(dependent.userId()).append("\n")
                .append("  ID решения (цель): ").append(dependent.solutionId()).append("\n")
                .append("  Процент совпадений: ").append(dependent.matchesPercentage()).append("%\n");
        if (addSourceCode) {
            mdBuilder.append("  #### Исходный код решения:\n\n");
            String dependentSourceCode = fetchDependentSourceCode(task, dependent, language);
            appendSourceCode(mdBuilder, dependentSourceCode);
        }
    }

    private String fetchDependentSourceCode(BaseTaskDto task, DependentTaskDto dependent, Language language) {
        return fileTrackerService.fetchSolutionContent(
                        task.taskId(),
                        dependent.userId(),
                        dependent.solutionId(),
                        language
                ).orElse("Исходный код не представлен");
    }
}



