package com.vk.codeanalysis.tokenizer;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class CollisionReport {
    private final Map<Long, Integer> collisions = new LinkedHashMap<>();

    private int totalFingerprints;

    public void addFingerprint() {
        totalFingerprints++;
    }

    public void addCollisionWith(Long solutionId) {
        collisions.compute(solutionId, (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
    }
}
