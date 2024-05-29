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
    private final Fingerprinter fingerprinter;
    private final Map<Integer, Map<Long, Long>> fingerprintBase = new HashMap<>();
    private final Map<Long, CollisionReport> reports = new HashMap<>();
    private final Set<Integer> ignoredFingerprints = new HashSet<>();

    public PlagiarismDetector(TSLanguage language) {
        TSParser tsParser = new TSParser();
        tsParser.setLanguage(language);
        this.fingerprinter = new Fingerprinter(tsParser);
    }

    public PlagiarismDetector(TSLanguage language, int k, int winnowLength) {
        TSParser tsParser = new TSParser();
        tsParser.setLanguage(language);
        this.fingerprinter = new Fingerprinter(tsParser, k, winnowLength);
    }

    public void processFile(long solutionId, String file) {
        Iterator<Integer> fingerprints = fingerprinter.getFingerprints(file);
        var collisionReport = new CollisionReport();
        Set<Integer> checkedFingerprints = new HashSet<>();
        while (fingerprints.hasNext()) {
            int fingerprint = fingerprints.next();
            collisionReport.addFingerprint();

            if (ignoredFingerprints.contains(fingerprint)) {
                continue;
            }

            Map<Long, Long> files = (fingerprintBase.containsKey(fingerprint))
                    ? fingerprintBase.get(fingerprint)
                    : new HashMap<>();

            for (Map.Entry<Long, Long> counter : files.entrySet()) {
                if (counter.getKey() != solutionId) {
                    collisionReport.addCollisionWith(counter.getKey());
                    if (!checkedFingerprints.contains(fingerprint)) {
                        reports.get(counter.getKey()).addManyCollisionsWith(
                                solutionId, Math.toIntExact(counter.getValue())
                        );
                        checkedFingerprints.add(fingerprint);
                    }
                }
            }
            files.put(solutionId, files.getOrDefault(solutionId, 0L) + 1);
            fingerprintBase.put(fingerprint, files);
        }

        reports.put(solutionId, collisionReport);
    }

    public void addIgnoredFile(String file) {
        Iterator<Integer> fingerprints = fingerprinter.getFingerprints(file);
        while (fingerprints.hasNext()) {
            int fingerprint = fingerprints.next();
            ignoredFingerprints.add(fingerprint);
        }
    }
}
