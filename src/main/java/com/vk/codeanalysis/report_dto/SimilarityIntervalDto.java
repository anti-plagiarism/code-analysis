package com.vk.codeanalysis.report_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

@Schema(description = "Интервал схожести решений")
@AllArgsConstructor
public class SimilarityIntervalDto {
    @JsonProperty(value = "start")
    float start;
    @JsonProperty(value = "end")
    float end;
}
