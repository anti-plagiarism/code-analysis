package com.vk.codeanalysis.rest.report;

import com.vk.codeanalysis.core.report_generator.MdReportGeneratorServiceImpl;
import com.vk.codeanalysis.dto.report.ReportDto;
import com.vk.codeanalysis.dto.request.ReportGetRequest;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v0/reports")
@RequiredArgsConstructor
@Tag(name = "Контролер отчётов", description = "Позволяет получить отчёты по загруженным решениям")
public class ReportController {
    private final DistributorServiceV0 distributorService;
    private final MdReportGeneratorServiceImpl mdReportGeneratorService;

    @GetMapping(value = "/md", produces = MediaType.TEXT_MARKDOWN_VALUE)
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решениям")
    public CompletableFuture<String> getMdReport(
            @RequestParam(name = "similarity_threshold_start", defaultValue = "0")
            @Parameter(description = "Нижний порог сходства")
            float similarityThresholdStart,
            @RequestParam(name = "similarity_threshold_end", defaultValue = "100")
            @Parameter(description = "Верхний порог сходства")
            float similarityThresholdEnd,
            @RequestBody ReportGetRequest request
    ) {
        return distributorService
                .getGeneralReport(
                        similarityThresholdStart,
                        similarityThresholdEnd,
                        request.tasks(),
                        request.users(),
                        request.langs())
                .thenApply(mdReportGeneratorService::convertToMarkdown);
    }

    @GetMapping("/json")
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решениям")
    public CompletableFuture<ReportDto> getJsonReport(
            @RequestParam(name = "similarity_threshold_start", defaultValue = "0")
            @Parameter(description = "Нижний порог сходства решений")
            float similarityThresholdStart,
            @RequestParam(name = "similarity_threshold_end", defaultValue = "100")
            @Parameter(description = "Верхний порог сходства решений")
            float similarityThresholdEnd,
            @RequestBody ReportGetRequest request
    ) {

        return distributorService
                .getGeneralReport(
                        similarityThresholdStart,
                        similarityThresholdEnd,
                        request.tasks(),
                        request.users(),
                        request.langs());
    }

    @PostMapping(path = "/private/md", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решению в формате MD")
    public CompletableFuture<String> getPrivateMdReport(
            @RequestParam("task_id") @Parameter(description = "ID задачи") long taskId,
            @RequestParam("solution_id") @Parameter(description = "ID решения") long solutionId,
            @RequestParam("user_id") @Parameter(description = "ID пользователя") long userId,
            @RequestParam("file") @Parameter(description = "Исходный код") MultipartFile file
    ) {
        return distributorService
                .getPrivateReport(taskId, solutionId, userId, file)
                .thenApply(mdReportGeneratorService::convertToMarkdown);
    }

    @PostMapping(path = "/private/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решению в формате JSON")
    public CompletableFuture<ReportDto> getPrivateJsonReport(
            @RequestParam("task_id") @Parameter(description = "ID задачи") long taskId,
            @RequestParam("solution_id") @Parameter(description = "ID решения") long solutionId,
            @RequestParam("user_id") @Parameter(description = "ID пользователя") long userId,
            @RequestParam("file") @Parameter(description = "Исходный код") MultipartFile file
    ) {

        return distributorService.getPrivateReport(taskId, solutionId, userId, file);
    }
}
