package com.vk.codeanalysis.rest.report;

import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.dto.report.ReportDto;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v0/reports")
@RequiredArgsConstructor
@Tag(name = "Контролер отчётов", description = "Позволяет получить отчёты по загруженным решениям")
public class ReportController {
    private final DistributorServiceV0 distributorService;

    @GetMapping("/md")
    @Operation(
            summary = "Получить отчет в формате MD",
            description = "Предоставляет отчёт в формате MD по загруженным раннее решениям")
    public CompletableFuture<String> getMdReport(
            @RequestParam(name = "similarity_threshold_start", defaultValue = "0")
            @Parameter(description = "Нижний порог сходства")
            float similarityThresholdStart,
            @RequestParam(name = "similarity_threshold_end", defaultValue = "100")
            @Parameter(description = "Верхний порог сходства")
            float similarityThresholdEnd,
            @RequestParam(name = "tasks", required = false)
            @Parameter(description = "Список идентификаторов задач")
            Set<Long> taskList,
            @RequestParam(name = "users", required = false)
            @Parameter(description = "Список идентификаторов пользователей")
            Set<Long> userList,
            @RequestParam(name = "langs", required = false)
            @Parameter(description = "Список языков программирования")
            Set<Language> langList
    ) {
        // TODO
        return null;
    }

    @GetMapping("/json")
    @Operation(
            summary = "Получить отчет в формате JSON",
            description = "Предоставляет отчёт в формате JSON по загруженным раннее решениям")
    @Async
    public CompletableFuture<ReportDto> getJsonReport(
            @RequestParam(name = "similarity_threshold_start", defaultValue = "0")
            @Parameter(description = "Нижний порог сходства решений")
            float similarityThresholdStart,
            @RequestParam(name = "similarity_threshold_end", defaultValue = "100")
            @Parameter(description = "Верхний порог сходства решений")
            float similarityThresholdEnd,
            @RequestParam(name = "tasks", required = false)
            @Parameter(description = "Список идентификаторов задач")
            Set<Long> tasks,
            @RequestParam(name = "users", required = false)
            @Parameter(description = "Список идентификаторов пользователей")
            Set<Long> users,
            @RequestParam(name = "langs", required = false)
            @Parameter(description = "Список языков программирования", example = "CPP,JAVA")
            Set<Language> langs
    ) {
        return distributorService.getGeneralReport(
                similarityThresholdStart,
                similarityThresholdEnd,
                tasks,
                users,
                langs);
    }

    @GetMapping("/private/md")
    @Operation(
            summary = "Получить отчет по конкретному решению",
            description = "Предоставляет отчёт по конкретному пользовательскому решению в формате MD")
    @Async
    public CompletableFuture<String> getPrivateMdReport(
            @RequestParam(name = "task_id")
            @Parameter(description = "Идентификатор задачи")
            long taskId,
            @RequestParam(name = "solution_id")
            @Parameter(description = "Идентификатор решения")
            long solutionId,
            @RequestParam(name = "user_id")
            @Parameter(description = "Идентификатор пользователя")
            long userId,
            @RequestParam(name = "lang")
            @Parameter(description = "Язык программирования", example = "CPP")
            Language lang,
            @RequestParam(name = "code")
            @Parameter(description = "Пользовательская программа", example = "CPP,JAVA")
            String code
    ) {
        // TODO
        return null;
    }

    @GetMapping("/private/json")
    @Operation(
            summary = "Получить отчет по конкретному решению",
            description = "Предоставляет отчёт по конкретному пользовательскому решению в формате JSON")
    @Async
    public CompletableFuture<ReportDto> getPrivateJsonReport(
            @RequestParam(name = "task_id")
            @Parameter(description = "Идентификатор задачи")
            long taskId,
            @RequestParam(name = "solution_id")
            @Parameter(description = "Идентификатор решения")
            long solutionId,
            @RequestParam(name = "user_id")
            @Parameter(description = "Идентификатор пользователя")
            long userId,
            @RequestParam(name = "lang")
            @Parameter(description = "Язык программирования", example = "CPP")
            Language lang,
            @RequestParam(name = "code")
            @Parameter(description = "Пользовательская программа")
            String code
    ) {
        return distributorService.getPrivateReport(
                taskId,
                solutionId,
                userId,
                lang,
                code);
    }
}
