package com.vk.codeanalysis.tokenizer;

import lombok.Getter;
import org.treesitter.TSLanguage;
import org.treesitter.TSParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class PlagiarismDetector {
    private static final int WINNOW_LENGTH = 5;

    private final Fingerprinter fingerprinter;
    private final Map<Integer, Set<Long>> fingerprintBase = new HashMap<>();
    private final Map<Long, CollisionReport> reports = new HashMap<>();

    // UserId <--> List<SolutionId>
    private final Map<Long, List<Long>> submittedSolutions = new HashMap<>();
    // SolutionId <--> UserId
    private final Map<Long, Long> solutionToUser = new HashMap<>();

    public PlagiarismDetector(TSLanguage language) {
        var tsParser = new TSParser();
        tsParser.setLanguage(language);
        this.fingerprinter = new Fingerprinter(tsParser);
    }

    public synchronized void processFile(long userId, long solutionId, String file) {
        List<Long> solutionsList = submittedSolutions.computeIfAbsent(userId, it -> new ArrayList<>());
        solutionsList.add(solutionId);
        solutionToUser.put(solutionId, userId);

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
