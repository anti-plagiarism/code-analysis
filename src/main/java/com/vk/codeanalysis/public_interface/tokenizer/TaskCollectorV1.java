package com.vk.codeanalysis.public_interface.tokenizer;

import com.vk.codeanalysis.tokenizer.PlagiarismDetector;

import java.util.Map;

public interface TaskCollectorV1 {
    void add(long taskId, long solutionId, String program);

    Map<Long, PlagiarismDetector> getDetectors();
}
