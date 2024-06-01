package com.vk.codeanalysis.public_interface.tokenizer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Поддерживаемые языки программирования
 */
@RequiredArgsConstructor
@Getter
public enum Language {
    JAVA("java"),
    CPP("cpp"),
    PY("py");

    private final String name;
}
