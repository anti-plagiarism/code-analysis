package com.vk.codeanalysis.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Schema(description = "Сущность отчета")
@Setter
@Getter
public class ReportDto {
    private SimilarityIntervalDto interval;
    private Set<Long> tasks;
    private Set<Long> users;
    private Set<String> languages;
    private Map<String, List<SimilarityDto>> body;
}
