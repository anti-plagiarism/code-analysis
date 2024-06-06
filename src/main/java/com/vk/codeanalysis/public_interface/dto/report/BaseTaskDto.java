package com.vk.codeanalysis.public_interface.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Сущность задачи, относительно которой находится схожесть")
@Builder
public record BaseTaskDto(
        @Schema(description = "Идентификатор решённого задания")
        @JsonProperty(value = "task_id")
        long taskId,
        @Schema(description = "Идентификатор пользователя, у которого могли украсть решение")
        @JsonProperty(value = "user_id")
        long userId,
        @Schema(description = "Идентификатор украденного решения")
        @JsonProperty(value = "solution_id")
        long solutionId,
        @Schema(description = "Список задач с которыми проведено сравнение текущей")
        @JsonProperty(value = "dependent_tasks")
        List<DependentTaskDto> dependentTasks
) {

}
