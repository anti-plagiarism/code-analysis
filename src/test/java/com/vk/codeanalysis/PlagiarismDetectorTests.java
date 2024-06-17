package com.vk.codeanalysis;

import com.vk.codeanalysis.public_interface.utils.FileUtils;
import com.vk.codeanalysis.tokenizer.PlagiarismDetector;
import com.vk.codeanalysis.tokenizer.Tuple;
import org.junit.jupiter.api.Test;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterCpp;
import org.treesitter.TreeSitterJava;
import org.treesitter.TreeSitterPython;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlagiarismDetectorTests {
    private static final int DEFAULT_K = 5;
    private static final int DEFAULT_WINNOW_LENGTH = 1;
    private static final double DELTA = 1e-6;

    @Test
    public void javaTest1() {
        File file1 = new File("res/test/653036_solution.java");
        File file2 = new File("res/test/646601_solution.java");
        testAll(new TreeSitterJava(), file1, file2, 0.9282296650717703, 0.8803827751196173);
    }

    @Test
    public void cppTest1() {
        File file1 = new File("res/test/649819_solution.cpp");
        File file2 = new File("res/test/647453_solution.cpp");
        testAll(new TreeSitterCpp(), file1, file2, 0.7908496732026143, 0.5847457627118644);
    }

    @Test
    public void pythonTest1() {
        File file1 = new File("res/test/653957_solution.py");
        File file2 = new File("res/test/652544_solution.py");
        testAll(new TreeSitterPython(), file1, file2, 0.363013698630137, 0.8723404255319149);
    }

    @Test
    public void javaTest2() {
        File file1 = new File("res/test/653036_solution.java");
        File file2 = new File("res/test/649804_Main.java");
        testAll(new TreeSitterJava(), file1, file2, 0.5885167464114832, 0.49572649572649574);
    }


    @Test
    public void javaTestSub() {
        File file1 = new File("res/test/649804_Main_Sub.java");
        File file2 = new File("res/test/649804_Main.java");
        testAll(new TreeSitterJava(), file1, file2, 0.9530201342281879, 0.6324786324786325);
    }


    private void testAll(TSLanguage language, File file1, File file2, double expected12, double expected21) {
        Tuple<Double, Double> result12 = getValue(language, file1, file2);
        Tuple<Double, Double> result21 = getValue(language, file2, file1);
        // checking values
        assertEquals(expected12, result12.getFirst(), DELTA);
        assertEquals(expected21, result12.getSecond(), DELTA);
        assertEquals(expected21, result21.getFirst(), DELTA);
        assertEquals(expected12, result21.getSecond(), DELTA);
        // checking self-collisions
        Tuple<Double, Double> result11 = getValue(language, file1, file1);
        assertEquals(result11.getFirst(), 1, DELTA);
        assertEquals(result11.getSecond(), 1, DELTA);
        Tuple<Double, Double> result22 = getValue(language, file1, file1);
        assertEquals(result22.getFirst(), 1, DELTA);
        assertEquals(result22.getSecond(), 1, DELTA);
    }

    private Tuple<Double, Double> getValue(TSLanguage tsLanguage, File file1, File file2) {
        return processFiles(new PlagiarismDetector(tsLanguage, DEFAULT_K, DEFAULT_WINNOW_LENGTH), file1, file2);
    }

    private Tuple<Double, Double> processFiles(PlagiarismDetector plagiarismDetector, File file1, File file2) {
        try {
            plagiarismDetector.processFile(1, 1, FileUtils.readFile(file1));
            plagiarismDetector.processFile(2, 2, FileUtils.readFile(file2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Tuple<>(
                plagiarismDetector.getReports().get(1L).getCollisions().get(2L) * 1.0d
                        / plagiarismDetector.getReports().get(1L).getTotalFingerprints(),
                plagiarismDetector.getReports().get(2L).getCollisions().get(1L) * 1.0d
                        / plagiarismDetector.getReports().get(2L).getTotalFingerprints()
        );

    }
}
