package com.vk.codeanalysis.plagiarismalg;

import lombok.Getter;
import org.treesitter.TSLanguage;
import org.treesitter.TSParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class PlagiarismDetector {
    private final Fingerprinter fingerprinter;
    private final Map<Integer, List<Long>> fingerprintBase = new HashMap<>();
    final Map<Long, CollisionReport> reports = new HashMap<>();

    public PlagiarismDetector(TSLanguage language) {
        TSParser tsParser = new TSParser();
        tsParser.setLanguage(language);
        this.fingerprinter = new Fingerprinter(tsParser);
    }

    public void processFile(long solutionId, String file) {
        Iterator<Integer> fingerprints = fingerprinter.getFingerprints(file);
        CollisionReport collisionReport = new CollisionReport();

        while (fingerprints.hasNext()) {
            int fingerprint = fingerprints.next();
            collisionReport.addFingerprint();

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
}
