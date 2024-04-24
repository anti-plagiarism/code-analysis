package com.vk.codeanalysis.plagiarismalg;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class CollisionReport {
    private final Map<Long, Integer> collisions;
    private int totalFingerprints;

    public CollisionReport() {
        collisions = new LinkedHashMap<>();
    }

    public void addFingerprint() {
        totalFingerprints++;
    }

    public void addCollisionWith(Long solutionId) {
        collisions.compute(solutionId, (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
    }
}
