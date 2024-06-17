package com.vk.codeanalysis.core.file_tracker;

import com.vk.codeanalysis.public_interface.dto.report.SolutionDto;
import com.vk.codeanalysis.public_interface.distributor.DistributorService;
import com.vk.codeanalysis.public_interface.file_tracker.FileTrackerService;
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

import static com.vk.codeanalysis.public_interface.utils.FileUtils.normalizePath;
import static com.vk.codeanalysis.public_interface.utils.FileUtils.readProgramFromFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileTrackerServiceImpl implements FileTrackerService {

    private static final int TASK_GROUP = 1;
    private static final int USER_GROUP = 2;
    private static final int SOLUTION_GROUP = 3;
    private static final int EXT_GROUP = 4;

    private final DistributorService distributorService;

    @Value("${file-tracker.path}")
    private String trackPath;
    @Value("${file-tracker.task-dir-suffix}")
    private String taskSuffix;
    @Value("${file-tracker.user-dir-suffix}")
    private String userSuffix;
    @Value("${file-tracker.solution-dir-suffix}")
    private String solutionSuffix;

    @PostConstruct
    private void trackSolutions() {
        Pattern solutionPathPattern = Pattern.compile(
                "/(\\d+)" + taskSuffix +
                        "/(\\d+)" + userSuffix +
                        "/(\\d+)" + solutionSuffix + "\\.(\\w+)");

        Path rootPath = Path.of(trackPath);

        try {
            Map<Long, SolutionDto> latestSolutions = new HashMap<>();
            Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        Optional<SolutionDto> solutionPutRequestOptional = parseSolutionPath(file, rootPath, solutionPathPattern);
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

            putSolutionsInDistributorService(latestSolutions);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Optional<SolutionDto> parseSolutionPath(Path solutionPath, Path rootPath, Pattern solutionPathPattern) {
        String normalizedPath = normalizePath(solutionPath);
        String regexToExclude = "^.+/" + rootPath.getFileName();
        String relativePath = normalizedPath.replaceFirst(regexToExclude, "");

        Matcher matcher = solutionPathPattern.matcher(relativePath);
        if (!matcher.matches()) {
            log.error("Path does not match: {}", relativePath);
            return Optional.empty();
        }

        return getSolutionEntity(matcher, solutionPath);
    }

    @Override
    public Optional<String> fetchSolutionContent(long taskId, long userId, long solutionId, Language language) {
        Path filePath = Path.of(trackPath)
                .resolve(taskId + taskSuffix)
                .resolve(userId + userSuffix)
                .resolve(solutionId + solutionSuffix + "." + language.getName());

        try {
            return readProgramFromFile(filePath);
        } catch (Exception e) {
            log.error("Файл не найден: {}", filePath);
            return Optional.empty();
        }
    }

    private void putSolutionsInDistributorService(Map<Long, SolutionDto> latestSolutions) {
        latestSolutions.values()
                .forEach(
                        solution ->
                                distributorService.put(
                                        solution.taskId(),
                                        solution.solutionId(),
                                        solution.userId(),
                                        solution.language(),
                                        solution.file()));
    }

    private Optional<SolutionDto> getSolutionEntity(Matcher matcher, Path solutionPath) {
        try {
            Language language = Language.valueOf(matcher.group(EXT_GROUP).toUpperCase());
            long taskId = Long.parseLong(matcher.group(TASK_GROUP));
            long userId = Long.parseLong(matcher.group(USER_GROUP));
            long solutionId = Long.parseLong(matcher.group(SOLUTION_GROUP));

            Optional<String> file = readProgramFromFile(solutionPath);

            return file.map(code -> SolutionDto.builder()
                    .taskId(taskId)
                    .userId(userId)
                    .solutionId(solutionId)
                    .language(language)
                    .file(code)
                    .build());

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
