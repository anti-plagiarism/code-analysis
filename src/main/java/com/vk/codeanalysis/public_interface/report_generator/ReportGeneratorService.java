package com.vk.codeanalysis.public_interface.report_generator;

import com.vk.codeanalysis.dto.report.ReportDto;
import com.vk.codeanalysis.public_interface.tokenizer.Language;

import java.util.Set;

public interface ReportGeneratorService {

    /**
     * Метод генерации общего отчета.
     * @param thresholdStart Нижний порог сходства решений
     * @param thresholdEnd Верхний порог сходства решений
     * @param tasks Список задач (учитывается если не равен null)
     * @param users Список пользователей (учитывается если не равен null)
     * @param langs Список языков (учитывается если не равен null)
     * @return ReportDto отчёт в формате json
     */
    ReportDto generateGeneralReport(
            float thresholdStart,
            float thresholdEnd,
            Set<Long> tasks,
            Set<Long> users,
            Set<Language> langs);

    /**
     * Метод генерации частного отчета.
     * @param taskId ID задачи
     * @param solutionId ID решения
     * @param userId ID пользователя
     * @param lang Язык программирования
     * @param code Само решение
     * @return ReportDto отчёт в формате json
     */
    ReportDto generatePrivateReport(
            long taskId,
            long solutionId,
            long userId,
            Language lang,
            String code);
}
