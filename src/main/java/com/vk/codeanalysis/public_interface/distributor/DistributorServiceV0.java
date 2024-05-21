package com.vk.codeanalysis.public_interface.distributor;

import com.vk.codeanalysis.public_interface.dto.SolutionPutRequest;
import com.vk.codeanalysis.report_dto.ReportDto;

import java.util.List;
import java.util.Set;

public interface DistributorServiceV0 {
    /**
     * Метод для загрузки файлов пользователей в систему антиплагиата
     * для последующего анализа
     * @param request данные, которые необходимо анализировать
     */
    void put(SolutionPutRequest request);

    /**
     * Получение отчёта, в котором будут находиться все решения,
     * которые имеют процент схожести больше либо равную, чем similarityThresholdStart
     * и меньше либо равную, чем similarityThresholdEnd
     * @param thresholdStart коэффициент (нижний порог), определяющий какие работы считать списывающими
     * @param thresholdEnd коэффициент (верхний порог), определяющий какие работы считать списывающими
     * @param tasksId коэффициент (верхний порог), определяющий какие работы считать списывающими
     * @param usersId коэффициент (верхний порог), определяющий какие работы считать списывающими
     * @param langs коэффициент (верхний порог), определяющий какие работы считать списывающими
     * @return Отчёт в строковом представлении
     */
    ReportDto getReport(
            float thresholdStart,
            float thresholdEnd,
            Set<Long> tasksId,
            Set<Long> usersId,
            Set<String> langs);
}
