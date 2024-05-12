package com.vk.codeanalysis.tokenizer;

import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV0;
import lombok.Getter;
import org.treesitter.TSLanguage;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TaskCollectorImpl implements TaskCollectorV0 {
    private final TSLanguage language;
    private final Map<Long, PlagiarismDetector> detectors = new HashMap<>();

    public TaskCollectorImpl(TSLanguage language) {
        this.language = language;
    }

    @Override
    public void add(long taskId, long userId, long solutionId, String program) {
        PlagiarismDetector detector = detectors.computeIfAbsent(taskId, id -> new PlagiarismDetector(language));
        detector.processFile(userId, solutionId, program);
    }
}
