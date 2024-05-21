package com.vk.codeanalysis.tokenizer;

import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV1;
import lombok.Getter;
import org.treesitter.TSLanguage;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TaskCollectorImpl implements TaskCollectorV1 {
    private final TSLanguage language;
    private final Map<Long, PlagiarismDetector> detectors = new HashMap<>();

    public TaskCollectorImpl(TSLanguage language) {
        this.language = language;
    }

    @Override
    public void add(long taskId, long solutionId, String program) {
        PlagiarismDetector detector = detectors.computeIfAbsent(taskId, id -> new PlagiarismDetector(language));
        detector.processFile(solutionId, program);
    }

    @Override
    public void addIgnored(long taskId, String program) {
        PlagiarismDetector detector = detectors.computeIfAbsent(taskId, id -> new PlagiarismDetector(language));
        detector.addIgnoredFile(program);
    }
}
