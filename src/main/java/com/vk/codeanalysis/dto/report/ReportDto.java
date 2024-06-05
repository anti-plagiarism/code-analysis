package com.vk.codeanalysis.dto.report;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Сущность отчета")
public class ReportDto {
    @Schema(description = "Интервал схожести решений")
    private SimilarityIntervalDto interval;
    @Schema(description = "Значения для идентификаторов заданий")
    private Set<Long> tasks;
    @Schema(description = "Значения для идентификаторов пользователей")
    private Set<Long> users;
    @Schema(description = "Значения для языков программирования")
    private Set<Language> languages;
    @Schema(description = "Мапа для хранения совпадений по пользовательским решениям, сгруппированным по языкам")
    private Map<Language, List<BaseTaskDto>> body;
}
