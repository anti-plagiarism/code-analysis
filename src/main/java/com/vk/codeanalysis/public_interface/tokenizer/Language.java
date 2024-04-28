package com.vk.codeanalysis.public_interface.tokenizer;

import lombok.Getter;

/**
 * Поддерживаемые языки программирования
 */
@Getter
public enum Language {
    JAVA("java"),
    CPP("cpp"),
    PYTHON("python");

    private final String name;

    Language(String name) {
        this.name = name;
    }
}
