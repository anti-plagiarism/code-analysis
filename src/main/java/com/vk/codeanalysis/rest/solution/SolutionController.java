package com.vk.codeanalysis.rest.solution;

import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.public_interface.dto.SolutionIgnoreRequest;
import com.vk.codeanalysis.public_interface.dto.SolutionPutRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/solutions")
@RequiredArgsConstructor
@Tag(name = "Контролер решений", description = "Позволяет загружать пользовательские решения для последующей проверки на антиплагиат")
public class SolutionController {
    private final DistributorServiceV0 distributorService;

    @PutMapping
    @Operation(summary = "Предоставить решение в обработку", description = "Позволяет загрузить пользовательское решение в систему")
    public ResponseEntity<String> putSolution(
            @RequestBody @Parameter(description = "Параметр для предоставления пользовательских решений") SolutionPutRequest request
    ) {
        distributorService.put(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/ignore")
    @Operation(summary = "Игнорировать решение", description = "Позволяет игнорировать пользовательское решение")
    public ResponseEntity<String> ignoreSolution(
            @RequestBody @Parameter(description = "Параметр для игнорирования пользовательских решений")
                SolutionIgnoreRequest request
    ) {
        distributorService.addIgnored(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
