package com.vk.codeanalysis.public_interface.report_generator;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.report_dto.ReportDto;

import java.util.Set;

public interface ReportGeneratorService {

    /**
     * Метод генерации общего отчета.
     * @param thresholdStart Нижний порог сходства решений
     * @param thresholdEnd Верхний порог сходства решений
     * @param tasks Список задач (учитывается если не равен null)
     * @param users Список пользователей (учитывается если не равен null)
     * @param langs Список языков (учитывается если не равен null)
     * @return ReportDto
     */
    ReportDto generateGeneralReport(
            float thresholdStart,
            float thresholdEnd,
            Set<Long> tasks,
            Set<Long> users,
            Set<String> langs);

    /**
     * Метод генерации частного отчета.
     * @param taskId ID задачи
     * @param solutionId ID решения
     * @param userId ID пользователя
     * @param lang Язык программирования
     * @param code Само решение
     * @return ReportDto
     */
    ReportDto generatePrivateReport(
            long taskId,
            long solutionId,
            long userId,
            String lang,
            String code);
}
