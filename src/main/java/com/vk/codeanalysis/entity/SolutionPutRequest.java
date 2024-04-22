package com.vk.codeanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SolutionPutRequest {
    private Long taskId;
    private Long solutionId;
    private String lang;
    private String program;
}
