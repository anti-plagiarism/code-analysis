package com.vk.codeanalysis.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Сущность для добавления решения")
@Builder
public record SolutionPutRequest(
        @Schema(description = "Идентификатор задания", example = "1")
        @JsonProperty(value = "task_id")
        Long taskId,
        @Schema(description = "Идентификатор пользователя из системы", example = "1")
        @JsonProperty(value = "user_id")
        Long userId,
        @Schema(description = "Идентификатор решения из системы", example = "1")
        @JsonProperty(value = "solution_id")
        Long solutionId,
        @Schema(description = "Язык программирования, на котором написана программа", example = "JAVA")
        Language lang,
        @Schema(description = "Текст программы")
        String program
) {

}
