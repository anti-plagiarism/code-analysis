package com.vk.codeanalysis.core.file_tracker;

import com.vk.codeanalysis.dto.report.SolutionDto;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vk.codeanalysis.Utils.FileUtils.normalizePath;
import static com.vk.codeanalysis.Utils.FileUtils.readProgramFromFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileTrackerService {
    private final DistributorServiceV0 distributorService;

    @Value("${file-tracker.path}")
    private String trackPath;

    private static final Pattern SOLUTION_PATH_PATTERN = Pattern.compile(
            "/(\\d+)_task/(\\d+)_user/(\\d+)_[-.\\w_()=\\s',]+\\.(\\w+)");
    private static final int TASK_GROUP = 1;
    private static final int USER_GROUP = 2;
    private static final int SOLUTION_GROUP = 3;
    private static final int EXT_GROUP = 4;

    @PostConstruct
    private void trackSolutions() {
        Path rootPath = Path.of(trackPath);
        try {
            Map<Long, SolutionDto> latestSolutions = new HashMap<>();
            Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        Optional<SolutionDto> solutionPutRequestOptional = parseSolutionPath(file, rootPath);
                        if (solutionPutRequestOptional.isEmpty()) {
                            return FileVisitResult.CONTINUE;
                        }
                        SolutionDto solutionPutRequest = solutionPutRequestOptional.get();
                        latestSolutions.merge(
                                solutionPutRequest.userId(),
                                solutionPutRequest,
                                (oldSolution, newSolution) -> oldSolution.solutionId() < newSolution.solutionId()
                                        ? newSolution
                                        : oldSolution);
                    } catch (Exception ignored) {
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            latestSolutions
                    .values()
                    .forEach(
                            solution ->
                                    distributorService.put(
                                            solution.taskId(),
                                            solution.solutionId(),
                                            solution.userId(),
                                            solution.language(),
                                            solution.file()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<SolutionDto> parseSolutionPath(Path solutionPath, Path rootPath) {
        String normalizedPath = normalizePath(solutionPath);
        String regexToExclude = "^.+/" + rootPath.getFileName();
        String relativePath = normalizedPath.replaceFirst(regexToExclude, "");

        Matcher matcher = SOLUTION_PATH_PATTERN.matcher(relativePath);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        return getSolutionEntity(matcher, solutionPath);
    }

    private Optional<SolutionDto> getSolutionEntity(Matcher matcher, Path solutionPath) {
        try {
            Language language = Language.valueOf(matcher.group(EXT_GROUP).toUpperCase());
            long taskId = Long.parseLong(matcher.group(TASK_GROUP));
            long userId = Long.parseLong(matcher.group(USER_GROUP));
            long solutionId = Long.parseLong(matcher.group(SOLUTION_GROUP));

            String file = readProgramFromFile(solutionPath);

            return Optional.of(
                    SolutionDto.builder()
                            .taskId(taskId)
                            .userId(userId)
                            .solutionId(solutionId)
                            .language(language)
                            .file(file)
                            .build());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<SolutionDto> fetchSolutionContent(long taskId, long userId, long solutionId) {
        Path rootPath = Path.of(trackPath);
        try {
            return Files.walk(rootPath)
                    .filter(Files::isRegularFile)
                    .map(this::parseSolutionPathHelper)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(solution -> solution.taskId() == taskId
                            && solution.userId() == userId
                            && solution.solutionId() == solutionId)
                    .findFirst();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    // метод, позволяющий избавиться от Files.walk
    public Optional<SolutionDto> fastFetchSolutionContent(long taskId, long userId, long solutionId) {

        String relativeFilePath = String.format("%d_task/%d_user/%d_solution.*", taskId, userId, solutionId);
        Path filePath = Path.of(trackPath, relativeFilePath).normalize();

        if (!Files.exists(filePath)) {
            log.error("Файл не найден: {}", filePath);
            return Optional.empty();
        }

        try {
            Matcher matcher = SOLUTION_PATH_PATTERN.matcher(filePath.toString());
            if (!matcher.matches()) {
                return Optional.empty();
            }
            Language language = Language.valueOf(matcher.group(EXT_GROUP).toUpperCase());


            String fileContent = readProgramFromFile(filePath);

            return Optional.of(SolutionDto.builder()
                    .taskId(taskId)
                    .userId(userId)
                    .solutionId(solutionId)
                    .language(language)
                    .file(fileContent)
                    .build());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<SolutionDto> parseSolutionPathHelper(Path file) {
        Path rootPath = Path.of(trackPath);
        return parseSolutionPath(file, rootPath);
    }

}
