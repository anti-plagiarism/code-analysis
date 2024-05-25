package com.vk.codeanalysis.core.report_generator;

import com.vk.codeanalysis.core.file_tracker.FileTrackerService;
import com.vk.codeanalysis.dto.report.BaseTaskDto;
import com.vk.codeanalysis.dto.report.ReportDto;
import com.vk.codeanalysis.dto.report.SimilarityIntervalDto;
import com.vk.codeanalysis.dto.report.SolutionDto;
import com.vk.codeanalysis.public_interface.tokenizer.Language;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MdReportGenerator {

    private final FileTrackerService fileTrackerService;

    public MdReportGenerator(FileTrackerService fileTrackerService) {
        this.fileTrackerService = fileTrackerService;
    }

    public String convertToMarkdown(ReportDto report) {
        StringBuilder mdBuilder = new StringBuilder();

        mdBuilder.append("# Отчёт по решениям\n\n");

        SimilarityIntervalDto interval = report.getInterval();
        mdBuilder.append("## Интервал схожести решений\n")
                .append("Старт: ").append(interval.start()).append("\n")
                .append("Конец: ").append(interval.end()).append("\n\n");

        Map<Language, List<BaseTaskDto>> body = report.getBody();
        body.forEach((language, tasks) -> {
            mdBuilder.append("## Язык программирования: ").append(language).append("\n\n");

            tasks.forEach(task -> {
                mdBuilder.append("### Задание ID: ").append(task.taskId()).append("\n")
                        .append("ID пользователя (источник): ").append(task.userId()).append("\n")
                        .append("ID решения (источник): ").append(task.solutionId()).append("\n")
                        .append("#### Исходный код решения:\n")
                        .append("\n");

                Optional<SolutionDto> solutionDto =
                        fileTrackerService.fetchSolutionContent(task.taskId(), task.userId(), task.solutionId());

                String sourceCode = solutionDto.map(SolutionDto::file).orElse("Исходный код не найден");

                mdBuilder.append("\n")
                        .append("<details>\n")
                        .append("<summary>Исходный код</summary>\n")
                        .append("<pre>\n")
                        .append(sourceCode).append("\n")
                        .append("</pre>\n")
                        .append("</details>\n\n");

                mdBuilder.append("#### Сходства с другими:\n");

                task.dependentTasks().forEach(dependent -> mdBuilder.append("- ID пользователя (цель): ").append(dependent.userId()).append("\n")
                        .append("  ID решения (цель): ").append(dependent.solutionId()).append("\n")
                        .append("  Процент совпадений: ").append(dependent.matchesPercentage()).append("%\n"));

                mdBuilder.append("\n");
            });
        });

        return mdBuilder.toString();
    }
}
