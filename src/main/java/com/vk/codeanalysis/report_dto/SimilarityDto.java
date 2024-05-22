package com.vk.codeanalysis.report_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сущность совпадений решений")
public record SimilarityDto(
        @JsonProperty(value = "task_id")
        long taskId,
        @JsonProperty(value = "user_src_id")
        long userSrcId,
        @JsonProperty(value = "solution_src_id")
        long solutionSrcId,
        @JsonProperty(value = "user_target_id")
        long userTargetId,
        @JsonProperty(value = "solution_target_id")
        long solutionTargetId,
        @JsonProperty(value = "matches_percentage")
        float matchesPercentage
) {

}
