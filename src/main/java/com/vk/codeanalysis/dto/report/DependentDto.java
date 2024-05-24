package com.vk.codeanalysis.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DependentDto (
    @Schema(description = "Идентификатор пользователя, который мог украсть решение")
    @JsonProperty(value = "user_target_id")
    long userTargetId,
    @Schema(description = "Идентификатор решения, который мы считаем сплагиаченным")
    @JsonProperty(value = "solution_target_id")
    long solutionTargetId,
    @Schema(description = "Процент совпадения между решениями")
    @JsonProperty(value = "matches_percentage")
    float matchesPercentage
) {

}
