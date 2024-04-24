package com.vk.codeanalysis.plagiarismalg;

import lombok.Getter;
import org.treesitter.TSLanguage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TaskCollector {
    private final TSLanguage language;
    private final Map<Long, PlagiarismDetector> detectors = new HashMap<>();

    public TaskCollector(TSLanguage language) {
        this.language = language;
    }

    public void add(long taskId, long solutionId, String program) throws IOException {
        PlagiarismDetector detector = detectors.computeIfAbsent(taskId, id -> new PlagiarismDetector(language));
        detector.processFile(solutionId, program);
    }
}
