package com.vk.codeanalysis.rest.report;

import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.public_interface.rest_report.ReportFormat;
import com.vk.codeanalysis.report_dto.ReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v0/reports")
@RequiredArgsConstructor
@Tag(name = "Контролер отчётов", description = "Позволяет получить отчёты по загруженным решениям")
public class ReportController {
    private final DistributorServiceV0 distributorService;

    @GetMapping
    @Operation(
            summary = "Получить отчет",
            description = "Предоставляет отчёт по решениям")
    public ResponseEntity<?> getJsonReport(
            @RequestParam(name = "format", defaultValue = "JSON")
            @Parameter(description = "Формат ответа: JSON или MD")
            ReportFormat format,
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
            Set<String> langList
    ) {

        ReportDto reportDto = distributorService
                .getReport(similarityThresholdStart,
                        similarityThresholdEnd,
                        taskList,
                        userList,
                        langList);

        switch (format) {
            case MD -> {
                // Здесь нужно наполнить отчет кодом и вернуть в формате MD.
                return null;
            }
            case JSON -> {
                return ResponseEntity.ok().body(reportDto);
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }
}
