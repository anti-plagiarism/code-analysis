package com.vk.codeanalysis.public_interface.tokenizer;

import com.vk.codeanalysis.tokenizer.PlagiarismDetector;

import java.util.Map;

public interface TaskCollectorV0 {
    void add(long taskId, long userId, long solutionId, String program);

    Map<Long, PlagiarismDetector> getDetectors();
}
