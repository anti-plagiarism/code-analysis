package com.vk.codeanalysis.plagiarismalg;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class CollisionReport {
    private final Map<Long, Integer> collisions;
    private int totalFingerprints = 0;

    public CollisionReport() {
        collisions = new LinkedHashMap<>();
    }

    public void addFingerprint() {
        totalFingerprints += 1;
    }

    public void addCollisionWith(Long solutionId) {
        int newCollisionCount = collisions.getOrDefault(solutionId, 0) + 1;
        collisions.put(solutionId, newCollisionCount);
    }
}
