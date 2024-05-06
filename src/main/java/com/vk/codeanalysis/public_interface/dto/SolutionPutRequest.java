package com.vk.codeanalysis.public_interface.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сущность для добавления решения")
public record SolutionPutRequest(
        @Schema(description = "Индетификатор задания", example = "1")
        @JsonProperty(value = "task_id")
        Long taskId,
        @Schema(description = "Индетификатор решения из системы", example = "1")
        @JsonProperty(value = "solution_id")
        Long solutionId,
        @Schema(description = "Язык программирования, на котором написана программа", example = "JAVA")
        Language lang,
        @Schema(description = "Текст программы")
        String program
) {

}
