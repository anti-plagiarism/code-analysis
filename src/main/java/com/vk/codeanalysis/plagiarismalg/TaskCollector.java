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
        PlagiarismDetector detector = detectors.get(taskId);
        if (detector == null) {
            detector = new PlagiarismDetector(language);
            detectors.put(taskId, detector);
        }

        detector.processFile(solutionId, program);
    }
}
