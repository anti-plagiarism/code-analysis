package com.vk.codeanalysis.public_interface.task_collector;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollector;

import java.util.Map;

public interface TaskCollectorFactoryService {
    /**
     * Получить опряделённый коллектор по языку программирования
     * @param language Язык программирования для определения коллектор
     * @return Полученный таск колектор
     */
    TaskCollector getCollector(Language language);

    Map<Language, TaskCollector> getTaskCollectors();
}
