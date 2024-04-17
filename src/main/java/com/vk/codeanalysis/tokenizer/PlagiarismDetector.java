package com.vk.codeanalysis.tokenizer;

import org.treesitter.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;

public class PlagiarismDetector {
    final Fingerprinter fingerprinter;
    final Map<Integer, Set<File>> fingerprintBase = new HashMap<>();

    public PlagiarismDetector(TSLanguage language) {
        TSParser tsParser = new TSParser();
        tsParser.setLanguage(language);
        fingerprinter = new Fingerprinter(tsParser);
    }

    public CollisionReport processFile(File file) throws IOException {
        Iterator<Integer> fingerprints = fingerprinter.getFingerprints(file);
        CollisionReport collisionReport = new CollisionReport();
        while (fingerprints.hasNext()) {
            int fingerprint = fingerprints.next();
            collisionReport.addFingerprint();
            Set<File> files;
            if (fingerprintBase.containsKey(fingerprint)) {
                files = fingerprintBase.get(fingerprint);
            } else {
                files = new HashSet<>();
            }
            for (File collisionFile : files) {
                if (!file.equals(collisionFile)) {
                    collisionReport.addCollisionWith(collisionFile);
                }
            }
            files.add(file);
            fingerprintBase.put(fingerprint, files);
        }
        return collisionReport;
    }

    public static void main(String[] args) throws IOException {
        PlagiarismDetector plagiarismDetector = new PlagiarismDetector(new TreeSitterPython());
        System.out.println("Input directory: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        Path file = Path.of(reader.readLine());
        Files.walkFileTree(file, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".py")) {
                    System.out.println(file);
                    try {
                        CollisionReport report = plagiarismDetector.processFile(file.toFile());
                        List<Map.Entry<File, Integer>> list = new ArrayList<>(report.getCollisions().entrySet());
                        list.sort(Map.Entry.comparingByValue());
                        System.out.println(report.getTotalFingerprints());
                        if (!list.isEmpty()) {
                            var entry = list.getLast();
                            System.out.println("\t" + entry.getKey() + ": " + entry.getValue()*1.0f / report.getTotalFingerprints());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return super.visitFile(file, attrs);
            }
        });
    }

}
