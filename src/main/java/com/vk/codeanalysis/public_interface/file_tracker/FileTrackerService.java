package com.vk.codeanalysis.public_interface.file_tracker;

import com.vk.codeanalysis.public_interface.tokenizer.Language;

import java.util.Optional;

public interface FileTrackerService {
    /**
     * Подгрузка отдельного решения из фаловой системы, указанной в переменных окружения
     * @param taskId Идентификатор номера задания
     * @param userId Идентификатор пользователя, отправившего задание
     * @param solutionId Идентификатор решения, которое отправил пользователь на конкретное задание
     * @param language Язык программирование, на котором написана программа
     * @return Текст пользовательского решение на задание
     */
    Optional<String> fetchSolutionContent(long taskId, long userId, long solutionId, Language language);
}
