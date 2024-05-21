package com.vk.codeanalysis.public_interface.report_generator;

import com.vk.codeanalysis.report_dto.ReportDto;

import java.util.Set;

public interface ReportGeneratorService {

    ReportDto generateReport(
            float thresholdStart,
            float thresholdEnd,
            Set<Long> tasksId,
            Set<Long> usersId,
            Set<String> langs);
}
