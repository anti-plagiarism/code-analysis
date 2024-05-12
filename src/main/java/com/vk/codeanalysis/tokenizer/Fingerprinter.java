package com.vk.codeanalysis.tokenizer;

import org.treesitter.TSInputEncoding;
import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSParser;
import org.treesitter.TSTree;
import org.treesitter.TreeSitterPython;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class Fingerprinter {
    private final static int K = 20;
    private final static int WINDOW_LENGTH = 5;

    private final TSParser tsParser;

    public Fingerprinter(TSParser tsParser) {
        this.tsParser = tsParser;
    }

    public Iterator<Integer> getFingerprints(String file, int winnowLength) {
        TSTree tree = tsParser.parseStringEncoding(null, file, TSInputEncoding.TSInputEncodingUTF8);
        KGram kGram = new KGram(K);

        TSTreeDFS dfsIterator = new TSTreeDFS(tree.getRootNode());

        MapIterator<TSNode, Integer> mapIterator = new MapIterator<>(
                dfsIterator,
                (node) -> {
                    kGram.put(node.getSymbol());
                    return kGram.getHashCode();
                }
        );

        return new WinnowingIterator(mapIterator, winnowLength);
    }

    private static String readFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder ans = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                ans.append(line);
                ans.append("\n");
            }
            return ans.toString();
        }
    }

    public Iterator<Integer> getFingerprintsFromFile(File file) throws IOException {
        TSTree tree = tsParser.parseStringEncoding(null, readFile(file), TSInputEncoding.TSInputEncodingUTF8);
        KGram kGram = new KGram(K);
        return new WinnowingIterator(
                new MapIterator<>(
                        new TSTreeDFS(tree.getRootNode()),
                        (node) -> {
                            kGram.put(node.getSymbol());
                            return kGram.getHashCode();
                        }
                ), 5
        );
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Input file: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        File file = new File(reader.readLine());
        TSParser tSparser = new TSParser();
        TSLanguage lang = new TreeSitterPython();
        tSparser.setLanguage(lang);
        Fingerprinter fingerprinter = new Fingerprinter(tSparser);
        Iterator<Integer> fingerprints = fingerprinter.getFingerprintsFromFile(file);
        while (fingerprints.hasNext()) {
            Integer fingerprint = fingerprints.next();
            System.out.println(fingerprint);
        }
    }
}
