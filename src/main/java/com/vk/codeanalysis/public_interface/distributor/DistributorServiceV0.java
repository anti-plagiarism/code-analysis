package com.vk.codeanalysis.public_interface.distributor;

import com.vk.codeanalysis.public_interface.dto.SolutionPutRequest;

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
     * @param similarityThresholdStart коэффициент (нижний порог), определяющий какие работы считать списывающими
     * @param similarityThresholdEnd коэффициент (верхний порог), определяющий какие работы считать списывающими
     * @return Отчёт в строковом представлении
     */
    String getReport(float similarityThresholdStart, float similarityThresholdEnd);
}
