package com.vk.codeanalysis.public_interface.distributor;

import com.vk.codeanalysis.report_dto.ReportDto;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface DistributorServiceV0 {

    /**
     * Метод для загрузки файлов пользователей в систему антиплагиата
     * для последующего анализа.
     * @param taskId
     * @param solutionId
     * @param userId
     * @param lang
     * @param code
     */
    void put(long taskId, long solutionId, long userId, String lang, String code);

    /**
     * Получение отчёта, в котором будут находиться все решения,
     * которые имеют процент схожести больше либо равную, чем similarityThresholdStart
     * и меньше либо равную, чем similarityThresholdEnd
     * @param thresholdStart коэффициент (нижний порог), определяющий какие работы считать списывающими
     * @param thresholdEnd коэффициент (верхний порог), определяющий какие работы считать списывающими
     * @param tasks список задач
     * @param users список пользователей
     * @param langs список языков программирования
     * @return Отчёт в строковом представлении
     */
    CompletableFuture<ReportDto> getGeneralReport(
            float thresholdStart,
            float thresholdEnd,
            Set<Long> tasks,
            Set<Long> users,
            Set<String> langs);

    /**
     * Получение частного отчёта
     * @param taskId ID задачи
     * @param solutionId ID решения
     * @param userId ID пользователя
     * @param lang язык программирования
     * @param code решение
     * @return
     */
    CompletableFuture<ReportDto> getPrivateReport(
            long taskId,
            long solutionId,
            long userId,
            String lang,
            String code);
    void addIgnored(SolutionIgnoreRequest request);
}
