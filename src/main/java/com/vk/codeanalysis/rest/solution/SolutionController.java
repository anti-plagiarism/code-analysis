package com.vk.codeanalysis.rest.solution;

import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.vk.codeanalysis.Utils.FileUtils.getLanguageFromExtension;
import static com.vk.codeanalysis.Utils.FileUtils.getProgram;

@RestController
@RequestMapping("/v0/solutions")
@RequiredArgsConstructor
@Tag(
        name = "Контролер решений",
        description = "Позволяет загружать решения для проверки на антиплагиат" +
                "или загружать авторские решения, чтобы избагать неоправданной блокировки"
)
public class SolutionController {
    private final DistributorServiceV0 distributorService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Предоставить решение в обработку", description = "Позволяет загрузить пользовательское решение в систему")
    public ResponseEntity<String> insertSolution(
            @RequestParam(value = "task_id")
            @Parameter(description = "Идентификатор задания", example = "1")
            Long taskId,
            @RequestParam(value = "solution_id")
            @Parameter(description = "Идентификатор решения из системы", example = "1")
            Long solutionId,
            @RequestParam(value = "user_id")
            @Parameter(description = "Идентификатор пользователя из системы", example = "1")
            Long userId,
            @RequestPart(value = "file") MultipartFile file
    ) {
        distributorService.put(
                taskId,
                solutionId,
                userId,
                getLanguageFromExtension(file),
                getProgram(file));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path = "/ignore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Игнорировать решение",
            description = "Позволяет игнорировать решения, которые автор задачи не считает сплагиаченными")
    public ResponseEntity<String> ignoreSolution(
            @RequestParam(value = "task_id")
            @Parameter(description = "Идентификатор задания", example = "1")
            Long taskId,
            @RequestParam("file") MultipartFile file
    ) {
        distributorService.addIgnored(taskId, file);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
