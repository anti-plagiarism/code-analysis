package com.vk.codeanalysis.core.file_tracker;

import com.vk.codeanalysis.public_interface.dto.SolutionPutRequest;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SolutionPathParser {
    private static final Pattern SOLUTION_PATH_PATTERN = Pattern.compile("/(\\d+)_task/(\\d+)_user/(\\d+)_solution\\.([a-zA-Z]+)");
    private static final int TASK_GROUP = 1;
    private static final int USER_GROUP = 2;
    private static final int SOLUTION_GROUP = 3;
    private static final int EXT_GROUP = 4;

    public static SolutionPutRequest parseSolutionPath(String solutionPath) {
        String normalizedPath = solutionPath.replace("\\", "/");
        String relativePath = normalizedPath.replaceFirst("^.+/anticheat_sample_solutions", "");
        Matcher matcher = SOLUTION_PATH_PATTERN.matcher(relativePath);
        if (matcher.matches()) {
            String languageExtension = matcher.group(EXT_GROUP);
            Language language = getLanguageFromExtension(languageExtension);
            if (language == null) {
                return null;
            }
            Long taskId = Long.parseLong(matcher.group(TASK_GROUP));
            Long userId = Long.parseLong(matcher.group(USER_GROUP));
            Long solutionId = Long.parseLong(matcher.group(SOLUTION_GROUP));
            String program = readProgramFromFile(solutionPath);
            String escapedProgram = escapeProgram(program);

            return new SolutionPutRequest(taskId, userId, solutionId, language, escapedProgram);
        } else {
            log.error("Path does not match");
            return null;
        }
    }

    private static Language getLanguageFromExtension(String extension) {
        for (Language language : Language.values()) {
            if (language.getName().equalsIgnoreCase(extension)) {
                return language;
            }
        }
        return null;
    }

    private static String escapeProgram(String program) {
        return program.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String readProgramFromFile(String solutionPath) {
        try {
            Path path = Path.of(solutionPath);
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read program from file: " + solutionPath, e);
        }
    }
}

