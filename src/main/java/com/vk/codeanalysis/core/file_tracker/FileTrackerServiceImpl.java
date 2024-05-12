package com.vk.codeanalysis.core.file_tracker;

import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.public_interface.file_tracker.FileTrackerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileTrackerServiceImpl implements FileTrackerService {

    private final ExecutorService executor;
    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;
    private final DistributorServiceV0 distributorService;

    @PostConstruct
    @Override
    public void trackSolutions() {
        Path rootPath = Path.of("C:/Users/dimas/Downloads/anticheat_sample_solutions");
        executor.execute(() -> {

            try {
                Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        processSolution(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                registerDirectories(rootPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                break;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();

                Path solutionPath = dir.resolve(filename);
                processSolution(solutionPath);
            }

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
        });

    }

    private void registerDirectories(Path rootPath) throws IOException {
        Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                keys.put(key, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void processSolution(Path solutionPath) {
        distributorService.put(SolutionPathParser.parseSolutionPath(solutionPath.toString()));
    }

}
