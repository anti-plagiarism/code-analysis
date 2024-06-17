package com.vk.codeanalysis.public_interface.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Сущность решения, которая сравнивается на схожесть")
@Builder
public record DependentSolutionDto(
        @Schema(description = "Идентификатор пользователя")
        @JsonProperty(value = "user_id")
        long userId,
        @Schema(description = "Идентификатор решения")
        @JsonProperty(value = "solution_id")
        long solutionId,
        @Schema(description = "Процент совпадения между решениями")
        @JsonProperty(value = "matches_percentage")
        float matchesPercentage
) {

}
