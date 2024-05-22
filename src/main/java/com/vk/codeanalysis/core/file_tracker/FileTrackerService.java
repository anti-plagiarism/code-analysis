package com.vk.codeanalysis.core.file_tracker;

import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.dto.request.SolutionPutRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FileTrackerService {
    private final DistributorServiceV0 distributorService;

    @Value("${file-tracker.path}")
    private String trackPath;

    @PostConstruct
    private void trackSolutions() {
        Path rootPath = Path.of(trackPath);
        try {
            Map<Long, SolutionPutRequest> latestSolutions = new HashMap<>();
            Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    Optional<SolutionPutRequest> solutionPutRequestOptional = SolutionPathParser.parseSolutionPath(file);
                    if (solutionPutRequestOptional.isEmpty()) {
                        return FileVisitResult.CONTINUE;
                    }
                    SolutionPutRequest solutionPutRequest = solutionPutRequestOptional.get();
                    latestSolutions.merge(
                            solutionPutRequest.userId(),
                            solutionPutRequest,
                            (oldSolution, newSolution) -> oldSolution.solutionId() < newSolution.solutionId()
                                    ? newSolution
                                    : oldSolution);
                    return FileVisitResult.CONTINUE;
                }
            });
            latestSolutions.values()
                    .forEach(distributorService::put);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
