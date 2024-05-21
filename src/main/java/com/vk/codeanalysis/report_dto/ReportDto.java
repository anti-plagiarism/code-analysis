package com.vk.codeanalysis.report_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Schema(description = "Сущность отчета")
@Setter
public class ReportDto {

    @JsonProperty(value = "interval")
    SimilarityIntervalDto interval;
    @JsonProperty(value = "tasks")
    Set<Long> tasks;
    @JsonProperty(value = "users")
    Set<Long> users;
    @JsonProperty(value = "languages")
    Set<String> languages;
    @JsonProperty(value = "body")
    Map<String, List<SimilarityDto>> body;

}
