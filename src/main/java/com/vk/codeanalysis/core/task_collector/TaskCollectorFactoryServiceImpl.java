package com.vk.codeanalysis.core.task_collector;

import com.vk.codeanalysis.public_interface.task_collector.TaskCollectorFactoryService;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollector;
import com.vk.codeanalysis.tokenizer.TaskCollectorImpl;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.treesitter.TSLanguage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskCollectorFactoryServiceImpl implements TaskCollectorFactoryService {
    private final Set<Language> languages;
    private final Map<Language, TSLanguage> treeSitter;
    @Getter
    private final Map<Language, TaskCollector> taskCollectors = new HashMap<>();

    @PostConstruct
    public void initCollectors() {
        for(Language lang : languages) {
            taskCollectors.put(lang, new TaskCollectorImpl(treeSitter.get(lang)));
        }
    }

    public TaskCollector getCollector(Language language) {
        TaskCollector collector = taskCollectors.get(language);

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }
        return collector;
    }
}
