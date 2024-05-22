package com.vk.codeanalysis.public_interface.distributor;

import com.vk.codeanalysis.dto.request.SolutionIgnoreRequest;
import com.vk.codeanalysis.dto.report.ReportDto;
import com.vk.codeanalysis.dto.request.SolutionPutRequest;
import com.vk.codeanalysis.public_interface.tokenizer.Language;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface DistributorServiceV0 {

    /**
     * Метод для загрузки файлов пользователей в систему антиплагиата
     * для последующего анализа.
     * @param request Параметр для предоставления пользовательских решений
     */
    void put(SolutionPutRequest request);

    /**
     * Получение отчёта, в котором будут находиться все решения,
     * которые имеют процент схожести больше либо равную, чем similarityThresholdStart
     * и меньше либо равную, чем similarityThresholdEnd
     * @param thresholdStart коэффициент (нижний порог), определяющий какие работы считать списывающими
     * @param thresholdEnd коэффициент (верхний порог), определяющий какие работы считать списывающими
     * @param tasks список задач
     * @param users список пользователей
     * @param langs список языков программирования
     * @return Отчёт в формате json
     */
    CompletableFuture<ReportDto> getGeneralReport(
            float thresholdStart,
            float thresholdEnd,
            Set<Long> tasks,
            Set<Long> users,
            Set<Language> langs);

    /**
     * Получение частного отчёта
     * @param taskId ID задачи
     * @param solutionId ID решения
     * @param userId ID пользователя
     * @param lang язык программирования
     * @param code решение
     * @return Отчёт в формате json
     */
    CompletableFuture<ReportDto> getPrivateReport(
            long taskId,
            long solutionId,
            long userId,
            Language lang,
            String code);

    /**
     *  Добавление решений, которые не следует считать плагиатом
     * @param request Данные для загрузки игнорируемового решения
     */
    void addIgnored(SolutionIgnoreRequest request);
}
