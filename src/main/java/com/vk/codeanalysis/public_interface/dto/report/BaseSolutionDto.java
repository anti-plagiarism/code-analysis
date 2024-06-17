package com.vk.codeanalysis.public_interface.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Сущность решения, с которыми производится сравнение")
@Builder
public record BaseSolutionDto(
        @Schema(description = "Идентификатор пользователя")
        @JsonProperty(value = "user_id")
        long userId,
        @Schema(description = "Идентификатор решения")
        @JsonProperty(value = "solution_id")
        long solutionId,
        @Schema(description = "Список задач, которые сравниваются")
        @JsonProperty(value = "dependent_tasks")
        List<DependentSolutionDto> dependentSolutions
) {

}
