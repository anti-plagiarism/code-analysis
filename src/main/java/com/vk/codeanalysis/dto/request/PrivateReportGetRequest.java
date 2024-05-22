package com.vk.codeanalysis.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "")
public record PrivateReportGetRequest(
        @JsonProperty("task_id")
        @Schema(description = "ID задачи")
        long taskId,
        @JsonProperty("solution_id")
        @Schema(description = "ID решения")
        long solutionId,
        @JsonProperty("user_id")
        @Schema(description = "ID пользователя")
        long userId,
        @JsonProperty("lang")
        @Schema(description = "Список языков")
        String lang,
        @JsonProperty("code")
        @Schema(description = "Список языков программирования")
        String code
) {

}
