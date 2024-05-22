package com.vk.codeanalysis.core.file_tracker;

import com.vk.codeanalysis.dto.request.SolutionPutRequest;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SolutionPathParser {
    private static final Pattern SOLUTION_PATH_PATTERN = Pattern.compile(
            "/(\\d+)_task/(\\d+)_user/(\\d+)_[-.\\w_()=\\s',]+\\.(\\w+)");
    private static final int TASK_GROUP = 1;
    private static final int USER_GROUP = 2;
    private static final int SOLUTION_GROUP = 3;
    private static final int EXT_GROUP = 4;

    public static Optional<SolutionPutRequest> parseSolutionPath(Path solutionPath) {
        String normalizedPath = solutionPath.toString().replace("\\", "/");
        String relativePath = normalizedPath.replaceFirst("^.+/anticheat_sample_solutions", "");
        Matcher matcher = SOLUTION_PATH_PATTERN.matcher(relativePath);
        if (!matcher.matches()) {
            log.error("Path does not match: {}", relativePath);
            return Optional.empty();
        }
        String languageExtension = matcher.group(EXT_GROUP);
        Language language = getLanguageFromExtension(languageExtension);
        if (language == null) {
            return Optional.empty();
        }
        long taskId = Long.parseLong(matcher.group(TASK_GROUP));
        long userId = Long.parseLong(matcher.group(USER_GROUP));
        long solutionId = Long.parseLong(matcher.group(SOLUTION_GROUP));
        String escapedProgram = readProgramFromFile(solutionPath);

        return Optional.of(SolutionPutRequest.builder()
                .taskId(taskId)
                .userId(userId)
                .solutionId(solutionId)
                .program(escapedProgram)
                .lang(language)
                .build());
    }

    private static Language getLanguageFromExtension(String extension) {
        for (Language language : Language.values()) {
            if (language.name().equalsIgnoreCase(extension)) {
                return language;
            }
        }
        return null;
    }

    private static String escapeProgram(String program) {
        return program.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String readProgramFromFile(Path solutionPath) {
        try (Stream<String> dataStream = Files.lines(solutionPath)) {
            return dataStream.map(SolutionPathParser::escapeProgram)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read program from file: " + solutionPath, e);
        }
    }
}

