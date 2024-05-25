package com.vk.codeanalysis.public_interface.tokenizer;

import lombok.RequiredArgsConstructor;

/**
 * Поддерживаемые языки программирования
 */
@RequiredArgsConstructor
public enum Language {
    JAVA("java"),
    CPP("cpp"),
    PY("py");

    private final String name;
}
