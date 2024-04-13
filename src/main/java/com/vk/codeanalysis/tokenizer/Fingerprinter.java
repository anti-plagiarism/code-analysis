package com.vk.codeanalysis.tokenizer;

import org.treesitter.*;

import java.io.*;
import java.util.Iterator;

public class Fingerprinter {
    private static final int DEFAULT_K = 20;
    private static final int DEFAULT_WINNOW_LENGTH = 5;
    private final TSParser tsParser;
    private final int k;
    private final int winnowLength;

    public Fingerprinter(TSParser tsParser) {
        this(tsParser, DEFAULT_K, DEFAULT_WINNOW_LENGTH);
    }

    public Fingerprinter(TSParser tsParser, int k, int winnowLength) {
        this.tsParser = tsParser;
        this.k = k;
        this.winnowLength = winnowLength;
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

    public Iterator<Integer> getFingerprints(File file) throws IOException {
        TSTree tree = tsParser.parseStringEncoding(null, readFile(file), TSInputEncoding.TSInputEncodingUTF8);
        KGram kGram = new KGram(k);
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
        Iterator<Integer> fingerprints = fingerprinter.getFingerprints(file);
        while (fingerprints.hasNext()) {
            Integer fingerprint = fingerprints.next();
            System.out.println(fingerprint);
        }
    }
}
