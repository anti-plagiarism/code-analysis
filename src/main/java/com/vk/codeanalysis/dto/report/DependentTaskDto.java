package com.vk.codeanalysis.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Сущность задачи, с которой сравниваются другие задачи на схожесть")
@Builder
public record DependentTaskDto(
        @Schema(description = "Идентификатор пользователя, который мог украсть решение")
        @JsonProperty(value = "user_id")
        long userId,
        @Schema(description = "Идентификатор решения, который мы считаем сплагиаченным")
        @JsonProperty(value = "solution_id")
        long solutionId,
        @Schema(description = "Процент совпадения между решениями")
        @JsonProperty(value = "matches_percentage")
        float matchesPercentage
) {

}
