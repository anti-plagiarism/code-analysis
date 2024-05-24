package com.vk.codeanalysis.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Сущность совпадений решений")
@Builder
public record BaseTaskDto(
        @Schema(description = "Идентификатор решённого задания")
        @JsonProperty(value = "task_id")
        long taskId,
        @Schema(description = "Идентификатор пользователя, у которого могли украсть решение")
        @JsonProperty(value = "user_src_id")
        long userSrcId,
        @Schema(description = "Идентификатор украденного решения")
        @JsonProperty(value = "solution_src_id")
        long solutionSrcId,

        @Schema(description = "")
        @JsonProperty(value = "dependent_dtos")
        List<DependentDto> dependentDtos

) {

}
