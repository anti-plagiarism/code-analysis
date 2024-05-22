package com.vk.codeanalysis.rest.report;

import com.vk.codeanalysis.dto.request.PrivateReportGetRequest;
import com.vk.codeanalysis.dto.request.ReportGetRequest;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.dto.report.ReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v0/reports")
@RequiredArgsConstructor
@Tag(name = "Контролер отчётов", description = "Позволяет получить отчёты по загруженным решениям")
public class ReportController {
    private final DistributorServiceV0 distributorService;

    @GetMapping("/md/")
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решениям")
    @Async
    public CompletableFuture<String> getMdReport(
            @RequestParam(name = "similarity_threshold_start", defaultValue = "0")
            @Parameter(description = "Нижний порог сходства")
            float similarityThresholdStart,
            @RequestParam(name = "similarity_threshold_end", defaultValue = "100")
            @Parameter(description = "Верхний порог сходства")
            float similarityThresholdEnd,
            @RequestBody
            ReportGetRequest request
    ) {
        // TODO
        return null;
    }

    @GetMapping("/json/")
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решениям")
    @Async
    public CompletableFuture<ReportDto> getJsonReport(
            @RequestParam(name = "similarity_threshold_start", defaultValue = "0")
            @Parameter(description = "Нижний порог сходства решений")
            float similarityThresholdStart,
            @RequestParam(name = "similarity_threshold_end", defaultValue = "100")
            @Parameter(description = "Верхний порог сходства решений")
            float similarityThresholdEnd,
            @RequestBody
            ReportGetRequest request
    ) {

        return distributorService
                .getGeneralReport(similarityThresholdStart,
                        similarityThresholdEnd,
                        request.tasks(),
                        request.users(),
                        request.langs());
    }

    @GetMapping("/private/md")
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решению в формате MD")
    @Async
    public CompletableFuture<String> getPrivateMdReport(
            @RequestBody
            PrivateReportGetRequest request
    ) {
        // TODO
        return null;
    }

    @GetMapping("/private/json")
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решению в формате JSON")
    @Async
    public CompletableFuture<ReportDto> getPrivateJsonReport(
            @RequestBody
            PrivateReportGetRequest request
    ) {

        return distributorService
                .getPrivateReport(request.taskId(),
                        request.solutionId(),
                        request.userId(),
                        request.lang(),
                        request.code());
    }
}
