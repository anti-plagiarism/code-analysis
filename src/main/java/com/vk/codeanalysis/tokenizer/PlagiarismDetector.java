package com.vk.codeanalysis.tokenizer;

import lombok.Getter;
import org.treesitter.TSLanguage;
import org.treesitter.TSParser;

import java.util.*;

@Getter
public class PlagiarismDetector {
    private static final int WINNOW_LENGTH = 5;

    private final Fingerprinter fingerprinter;
    private final Map<Integer, List<Long>> fingerprintBase = new HashMap<>();
    private final Map<Long, CollisionReport> reports = new HashMap<>();
    private final Set<Integer> ignoredFingerprints = new HashSet<>();

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

            if (ignoredFingerprints.contains(fingerprint)) {
                continue;
            }

            List<Long> files = (fingerprintBase.containsKey(fingerprint))
                    ? fingerprintBase.get(fingerprint)
                    : new LinkedList<>();

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

    public void addIgnoredFile(String file) {
        Iterator<Integer> fingerprints = fingerprinter.getFingerprints(file, WINNOW_LENGTH);
        while (fingerprints.hasNext()) {
            int fingerprint = fingerprints.next();
            ignoredFingerprints.add(fingerprint);
        }
    }
}
