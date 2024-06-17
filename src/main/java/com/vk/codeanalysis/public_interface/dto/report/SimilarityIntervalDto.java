package com.vk.codeanalysis.public_interface.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Интервал схожести решений")
public record SimilarityIntervalDto (
     @Schema(description = "Начало интервала") float start,
     @Schema(description = "Конец интервала") float end
) {

}
