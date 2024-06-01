package com.vk.codeanalysis.public_interface.tokenizer;

import com.vk.codeanalysis.tokenizer.PlagiarismDetector;

import java.util.Map;

public interface TaskCollector {
    void add(long taskId, long solutionId, long userId, String program);

    void addIgnored(long taskId, String program);

    Map<Long, PlagiarismDetector> getDetectors();
}
