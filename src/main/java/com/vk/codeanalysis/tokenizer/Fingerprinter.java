package com.vk.codeanalysis.tokenizer;

import org.springframework.beans.factory.annotation.Value;
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
    private final TSParser tsParser;

    @Value("${fingerprinter.k}")
    private int k;

    @Value("${fingerprinter.window-length}")
    private int windowLength;

    public Fingerprinter(TSParser tsParser) {
        this.tsParser = tsParser;
    }

    public Fingerprinter(TSParser tsParser, int k, int windowLength) {
        this.tsParser = tsParser;
        this.k = k;
        this.windowLength = windowLength;
    }

    public Iterator<Integer> getFingerprints(String file) {
        TSTree tree = tsParser.parseStringEncoding(null, file, TSInputEncoding.TSInputEncodingUTF8);
        KGram kGram = new KGram(k);

        TSTreeDFS dfsIterator = new TSTreeDFS(tree.getRootNode());

        MapIterator<TSNode, Integer> mapIterator = new MapIterator<>(
                dfsIterator,
                (node) -> {
                    kGram.put(node.getSymbol());
                    return kGram.getHashCode();
                }
        );

        return new WinnowingIterator(mapIterator, windowLength);
    }

    public Iterator<Integer> getFingerprintsFromFile(File file) throws IOException {
        TSTree tree = tsParser.parseStringEncoding(null, Utils.readFile(file), TSInputEncoding.TSInputEncodingUTF8);
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
        Iterator<Integer> fingerprints = fingerprinter.getFingerprintsFromFile(file);
        while (fingerprints.hasNext()) {
            Integer fingerprint = fingerprints.next();
            System.out.println(fingerprint);
        }
    }
}
