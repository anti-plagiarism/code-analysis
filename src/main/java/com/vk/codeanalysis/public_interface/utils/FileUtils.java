package com.vk.codeanalysis.public_interface.utils;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class FileUtils {

    private FileUtils() {

    }

    public static String escapeProgram(String program) {
        return program
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String normalizePath(Path path) {
        return path.toString().replace("\\", "/");
    }

    public static Optional<String> readProgramFromFile(Path solutionPath) {
        try (Stream<String> dataStream = Files.lines(solutionPath)) {
            String code = dataStream.map(FileUtils::escapeProgram)
                    .collect(Collectors.joining("\n"));
            return Optional.of(code);
        } catch (IOException e) {
            log.error("Failed to read program from file: " + solutionPath);
            return Optional.empty();
        }
    }

    public static Language getLanguageFromExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (extension == null) {
            throw new IllegalArgumentException("File is not valid, change extension of this file");
        }
        return Language.valueOf(extension.toUpperCase());
    }

    public static String getProgram(MultipartFile file) {
        try {
            String code = new String(file.getBytes(), StandardCharsets.UTF_8);
            return escapeProgram(code);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String readFile(File file) throws IOException {
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
}

