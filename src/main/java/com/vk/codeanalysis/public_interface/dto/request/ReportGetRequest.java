package com.vk.codeanalysis.public_interface.dto.request;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Параметры включаемых в отчёт решений")
public record ReportGetRequest(
        @Schema(description = "Список идентификаторов задач")
        Set<Long> tasks,
        @Schema(description = "Список идентификаторов пользователей")
        Set<Long> users,
        @Schema(description = "Список языков программирования")
        Set<Language> langs
) {

}
