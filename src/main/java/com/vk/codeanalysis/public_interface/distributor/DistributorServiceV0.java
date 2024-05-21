package com.vk.codeanalysis.public_interface.distributor;

import com.vk.codeanalysis.public_interface.dto.SolutionIgnoreRequest;
import com.vk.codeanalysis.public_interface.dto.SolutionPutRequest;

public interface DistributorServiceV0 {
    /**
     * Метод для загрузки файлов пользователей в систему анитиплагиата
     * для последущего анализа
     * @param request данные, которые необходимо анализировать
     */
    void put(SolutionPutRequest request);

    void addIgnored(SolutionIgnoreRequest request);
    /**
     * Получение отчёта, в котором будут находиться все решения,
     * которые имеют схожесть больше, чем similarityThreshold
     * @param similarityThreshold коэффициент, определяющий какие работы считать списывающими
     * @return Отчёт в строковом представлении
     */
    String get(float similarityThreshold);
}
