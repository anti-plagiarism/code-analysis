package com.vk.codeanalysis.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmptyFileException extends RuntimeException {

    public EmptyFileException(String message) {
        super(message);
    }
}
