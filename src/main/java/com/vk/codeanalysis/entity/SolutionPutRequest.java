package com.vk.codeanalysis.entity;

public record SolutionPutRequest(
        Long taskId,
        Long solutionId,
        String lang,
        String program
) {

}
