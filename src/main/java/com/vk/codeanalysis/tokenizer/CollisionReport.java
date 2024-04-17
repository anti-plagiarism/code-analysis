package com.vk.codeanalysis.tokenizer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CollisionReport {
    private final Map<File, Integer> collisions;
    private int totalFingerprints = 0;


    public CollisionReport() {
        collisions = new HashMap<>();
    }

    public void addFingerprint() {
        totalFingerprints += 1;
    }

    public void addCollisionWith(File file) {
        int newCollisionCount = collisions.getOrDefault(file, 0) + 1;
        collisions.put(file, newCollisionCount);
    }

    public Map<File, Integer> getCollisions() {
        return collisions;
    }

    public int getTotalFingerprints() {
        return totalFingerprints;
    }
}
