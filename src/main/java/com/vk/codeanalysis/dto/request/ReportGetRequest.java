package com.vk.codeanalysis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "")
public record ReportGetRequest(
        @Schema(description = "Список идентификаторов задач")
        Set<Long> tasks,
        @Schema(description = "Список идентификаторов пользователей")
        Set<Long> users,
        @Schema(description = "Список языков программирования")
        Set<String> langs
) {

}
