package com.vk.codeanalysis.public_interface.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Сущность задачи")
@Builder
public record TaskDto(
        @Schema(description = "Идентификатор задания")
        @JsonProperty(value = "task_id")
        long taskId,
        @Schema(description = "Список задач, с которым проводится сравнения")
        @JsonProperty(value = "base_tasks")
        List<BaseSolutionDto> baseSolutions
) {

}
