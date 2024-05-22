package com.vk.codeanalysis.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сущность для игнорируемых решений, которые не считаются плагиатом")
public record SolutionIgnoreRequest (
    @Schema(description = "Идентификатор задания", example = "1")
    @JsonProperty(value = "task_id")
    Long taskId,
    @Schema(description = "Язык программирования, на котором написана программа", example = "JAVA")
    Language lang,
    @Schema(description = "Текст программы")
    String program
) {

}
