package com.vk.codeanalysis.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Интервал схожести решений")
public record SimilarityIntervalDto (
     float start,
     float end
) {

}
