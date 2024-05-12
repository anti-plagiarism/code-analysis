package com.vk.codeanalysis.rest.report;

import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/reports")
@RequiredArgsConstructor
@Tag(name = "Контролер отчётов", description = "Позволяет получить отчёты по загруженным решениям")
public class ReportController {
    private final DistributorServiceV0 distributorService;

    @GetMapping
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решениям, у которых схожесть больше, чем заданный коэффициент")
    public ResponseEntity<String> getReport(
            @RequestParam(name = "similarity_threshold_start", defaultValue = "0")
            @Parameter(description = "Нижний порог сходства")
            float similarityThresholdStart,
            @RequestParam(name = "similarity_threshold_end", defaultValue = "100")
            @Parameter(description = "Верхний порог сходства")
            float similarityThresholdEnd
    ) {
        String report = distributorService.getReport(similarityThresholdStart, similarityThresholdEnd);
        return ResponseEntity.ok().body(report);
    }
}
