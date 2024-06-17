package com.vk.codeanalysis.public_interface.dto.report;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Сущность предоставляемого решения")
@Builder
public record SolutionDto(
        @Schema(description = "Идентификатор задания", example = "1")
        Long taskId,
        @Schema(description = "Идентификатор задания", example = "1")
        Long userId,
        @Schema(description = "Идентификатор задания", example = "1")
        Long solutionId,
        @Schema(description = "Язык программирования, на котором написана программа", example = "java")
        Language language,
        @Schema(description = "Текст программы")
        String file
) {

}
