package com.vk.codeanalysis.tokenizer;

import lombok.Getter;
import org.treesitter.TSLanguage;
import org.treesitter.TSParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Getter
public class PlagiarismDetector {
    private static final int WINNOW_LENGTH = 5;

    private final Fingerprinter fingerprinter;
    private final Map<Integer, Set<Long>> fingerprintBase = new HashMap<>();
    private final Map<Long, CollisionReport> reports = new HashMap<>();

    public PlagiarismDetector(TSLanguage language) {
        var tsParser = new TSParser();
        tsParser.setLanguage(language);
        this.fingerprinter = new Fingerprinter(tsParser);
    }

    public void processFile(long solutionId, String file) {
        Iterator<Integer> fingerprints = fingerprinter.getFingerprints(file, WINNOW_LENGTH);
        var collisionReport = new CollisionReport();

        while (fingerprints.hasNext()) {
            int fingerprint = fingerprints.next();
            collisionReport.addFingerprint();

            Set<Long> files = (fingerprintBase.containsKey(fingerprint))
                    ? fingerprintBase.get(fingerprint)
                    : new HashSet<>();

            for (Long collisionSolutionId : files) {
                if (collisionSolutionId != solutionId) {
                    collisionReport.addCollisionWith(collisionSolutionId);
                    reports.get(collisionSolutionId).addCollisionWith(solutionId);
                }
            }

            files.add(solutionId);
            fingerprintBase.put(fingerprint, files);
        }

        reports.put(solutionId, collisionReport);
    }
}
