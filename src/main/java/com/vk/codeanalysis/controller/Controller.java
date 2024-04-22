package com.vk.codeanalysis.controller;

import com.vk.codeanalysis.entity.SolutionGetRequest;
import com.vk.codeanalysis.entity.SolutionPutRequest;
import com.vk.codeanalysis.service.DistributorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/solution")
@AllArgsConstructor
public class Controller {

    private final DistributorService distributorService;

    @PutMapping
    @Operation(summary = "Предоставить решение в обработку")
    public ResponseEntity<String> putSolution(@RequestBody SolutionPutRequest request) {
        try {
            distributorService.put(request);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Получить отчет")
    public ResponseEntity<String> getReport(@RequestBody SolutionGetRequest request) {
        float similarityThreshold = request.getSimilarityThreshold();
        if (similarityThreshold < 0 || similarityThreshold > 100) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String report = distributorService.get(request);
        return ResponseEntity.ok().body(report);
    }
}
